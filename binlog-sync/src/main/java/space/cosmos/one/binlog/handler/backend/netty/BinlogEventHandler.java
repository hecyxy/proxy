package space.cosmos.one.binlog.handler.backend.netty;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.binlog.handler.factory.BackendConnection;

public class BinlogEventHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(BinlogEventHandler.class);

    private BackendConnection source;

    public BinlogEventHandler(BackendConnection source) {
        this.source = source;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    }

    private void handleEvent() {

    }

    private void handleError() {

    }
}
