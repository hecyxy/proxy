package space.cosmos.one.mysql.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.mysql.codec.Parser;
import space.cosmos.one.mysql.constant.RemotingType;

import java.net.InetSocketAddress;

public class FrontedHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(FrontedHandler.class);
    private ConnectionConfig config;
    private Parser parser;

    private Channel proxy2Server;

    FrontedHandler(ConnectionConfig config) {
        this.config = config;
        this.parser = config.getParser();
        //record
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            config.getMpsc().add((ByteBuf) msg);
        } catch (Exception e) {
            logger.warn("add bytebuf error", e);
        }
        logger.info("front receive msg");
        proxy2Server.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                //todo
                ctx.channel().read();
            } else {
                logger.error("write error");
                ctx.channel().close();
            }
        });
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        InetSocketAddress userAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        //set user ip
        parser.setUserAddress(userAddress.getHostString());
        Bootstrap boot = new Bootstrap();
        boot.group(ctx.channel().eventLoop())
                .channel(ctx.channel().getClass())
                .remoteAddress(config.getBackend())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast("backend logging", new LoggingHandler());
                        ch.pipeline().addLast("backend handler", new BackendHandler(ctx.channel(), config.getParser()));
                    }
                });
        ChannelFuture future = boot.connect();
        proxy2Server = future.channel();
        future.addListener((ChannelFutureListener) listener -> {
            if (listener.isSuccess()) {
                logger.info(String.format("user address %s mysql server %s", ctx.channel().remoteAddress(), config.getBackend()));
                ctx.channel().read();
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
        proxy2Server.close();
        logger.info(String.format("user close connection %s", ctx.channel().remoteAddress()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
