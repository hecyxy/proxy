package space.cosmos.one.mysql.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.mysql.util.ByteStream;
import space.cosmos.one.mysql.util.WrapStream;
import space.cosmos.one.mysql.util.Pair;

import java.net.InetSocketAddress;

/**
 * 复用NioEventLoopGroup 避免线程膨胀
 */
public class FrontedHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(FrontedHandler.class);
    private ConnectionConfig config;

    private Channel proxy2Server;

    private WrapStream cmdInfo = new WrapStream();

    private Bootstrap bootstrap = new Bootstrap();

    FrontedHandler(ConnectionConfig config) {
        this.config = config;
        //record
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//        while (isReadable((ByteBuf) msg)) {
        try {
            config.getMap().get(ctx.channel()).getProducerQueue().add(new Pair(ByteStream.REQUEST, ((ByteBuf) msg).copy()));
        } catch (Throwable t) {
            logger.warn("front add byte buf error", t.getCause());
        }

        logger.info("address " + ctx.channel().remoteAddress());
        logger.info("front receive msg");
        proxy2Server.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                logger.info("flush success");
            } else {
                logger.error("write error");
                ctx.channel().close();
            }
        });
//            return;
//        }

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        config.getMap().putIfAbsent(ctx.channel(), cmdInfo);
        InetSocketAddress userAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String host = userAddress.getHostString();
        cmdInfo.setRemoteAddress(host);
        bootstrap.group(ctx.channel().eventLoop())
                .channel(ctx.channel().getClass())
                .remoteAddress(config.getBackend())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
//                        ch.pipeline().addLast("backend logging", new LoggingHandler());
                        ch.pipeline().addLast("decoder", new MysqlDecoder());
                        ch.pipeline().addLast("backend handler", new BackendHandler(ctx.channel(), cmdInfo));
                    }
                });
        ChannelFuture future = bootstrap.connect();
        proxy2Server = future.channel();
        future.addListener((ChannelFutureListener) listener -> {
            if (listener.isSuccess()) {
                logger.info(String.format("user address %s mysql server %s", ctx.channel().remoteAddress(), config.getBackend()));
            } else {
                if (ctx.channel().isOpen()) {
                    ctx.channel().close();
                }
            }
        });
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        super.channelInactive(ctx);
        if (proxy2Server != null) {
            proxy2Server.close();
        }
//        config.getMap().remove(ctx.channel());
        logger.info(String.format("user close connection %s", ctx.channel().remoteAddress()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

}
