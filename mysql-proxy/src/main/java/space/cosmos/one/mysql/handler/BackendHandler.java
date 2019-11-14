package space.cosmos.one.mysql.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jctools.queues.MpscChunkedArrayQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.mysql.codec.Parser;
import space.cosmos.one.mysql.constant.RemotingType;
import space.cosmos.one.mysql.util.CmdInfo;

public class BackendHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(BackendHandler.class);

    private Channel user2Proxy;
    private CmdInfo cmdInfo;

    public BackendHandler(Channel user2Proxy, CmdInfo cmdInfo) {
        this.user2Proxy = user2Proxy;
        this.cmdInfo = cmdInfo;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("backend receive msg,{}", ((ByteBuf) msg).readableBytes());
        try {
            cmdInfo.getProducerQueue().add(((ByteBuf) msg).copy());
        } catch (Throwable t) {
            logger.warn("backend writer buffer error", t.getCause());
        }
        user2Proxy.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
