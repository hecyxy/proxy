package space.cosmos.one.mysql.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class FrontedHandler extends ChannelInboundHandlerAdapter {
    private ConnectionConfig confg;

    public FrontedHandler(ConnectionConfig config) {
        this.confg = config;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    }

    public void channelRegistered() {

    }

    public void channelUnregistered() {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
