package space.cosmos.one.binlog.handler.backend.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.binlog.handler.request.AuthRequest;
import space.cosmos.one.binlog.handler.request.CommandRequest;
import space.cosmos.one.binlog.handler.request.DumpBinaryLogCommand;
import space.cosmos.one.common.packet.message.Command;
import space.cosmos.one.common.util.BufferUtils;

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
        } else if (msg instanceof DumpBinaryLogCommand) {
            logger.info("dump binary log cmd");
            encodeDumpBinaryCmd(send, (DumpBinaryLogCommand) msg);
            ctx.write(send);

        } else {
            throw new RuntimeException("unknown request");
        }
    }

    public void encodeDumpBinaryCmd(ByteBuf buffer, DumpBinaryLogCommand dblc) {
        BufferUtils.writeUB3(buffer, 1 + 4 + 2 + 4 + dblc.getBinlogFilename().length());
        /**
         * The sequence-id is incremented with each packet and may wrap around.
         * It starts at 0 and is reset to 0 when a new command begins in the Command Phase
         */
        buffer.writeByte(0);//dblc.getSequenceNo(); packet no
        BufferUtils.writeInteger(buffer, Command.BINLOG_DUMP.code(), 1);
        BufferUtils.writeUB4(buffer, dblc.getBinlogPosition());
        // 00 flag
        buffer.writeByte(0);
        buffer.writeByte(0);
        BufferUtils.writeUB4(buffer, dblc.getServerId());
        buffer.writeBytes(dblc.getBinlogFilename().getBytes());
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
