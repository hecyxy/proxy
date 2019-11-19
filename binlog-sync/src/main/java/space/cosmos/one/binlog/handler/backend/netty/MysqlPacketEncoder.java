package space.cosmos.one.binlog.handler.backend.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.binlog.handler.request.AuthRequest;
import space.cosmos.one.binlog.handler.request.CommandRequest;

public class MysqlPacketEncoder extends ChannelOutboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(MysqlPacketDecoder.class);


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        logger.info("receive write ...");
        ByteBuf send = ctx.alloc().buffer();
        if (msg instanceof AuthRequest) {
            try {
                encode((AuthRequest) msg, send);
                ctx.write(send);
            } finally {
                ((AuthRequest) msg).getBody().release();
            }
        } else if (msg instanceof CommandRequest) {
            try {
                encode((CommandRequest) msg, send);
                ctx.write(send);
            } finally {
                ((CommandRequest) msg).getData().release();
            }
        } else {
            throw new RuntimeException("unknown request");
        }
    }

    private void encode(AuthRequest msg, ByteBuf buf) {
        buf.writeMediumLE(msg.getBody().readableBytes());
        buf.writeByte(msg.getSequenceNo());
        buf.writeBytes(msg.getBody());
    }

    private void encode(CommandRequest msg, ByteBuf buf) {
        buf.writeMediumLE(1 + msg.getData().readableBytes());
        buf.writeByte(msg.getSequenceNo());
        buf.writeByte(msg.getCmd().code());
        buf.writeBytes(msg.getData());
    }
}
