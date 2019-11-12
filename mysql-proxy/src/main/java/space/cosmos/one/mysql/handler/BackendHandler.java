package space.cosmos.one.mysql.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.mysql.codec.Parser;
import space.cosmos.one.mysql.constant.RemotingType;

public class BackendHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(BackendHandler.class);

    private Channel user2Proxy;
    private Parser parser;

    public BackendHandler(Channel user2Proxy, Parser parser) {
        this.user2Proxy = user2Proxy;
        this.parser = parser;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        parser.parse((ByteBuf) msg, RemotingType.PROXY_TO_SERVER);
        logger.info("backend receive msg");
        user2Proxy.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
