package space.cosmos.one.binlog.handler.backend.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.binlog.handler.factory.BackendConnection;
import space.cosmos.one.common.packet.binlog.Event;

import static space.cosmos.one.common.packet.message.ResponseType.RESPONSE_ERROR;
import static space.cosmos.one.common.packet.message.ResponseType.RESPONSE_OK;

public class BinlogEventHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(BinlogEventHandler.class);

    private BackendConnection source;

    public BinlogEventHandler(BackendConnection source) {
        this.source = source;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        short packType = buf.getUnsignedByte(buf.readerIndex() + 4);
        switch (packType) {
            case 0xff://ErrorPacket.FIELD_COUNT
                logger.info("auth ok ...");
                break;
            case RESPONSE_ERROR:
                logger.info("error shut down");
                break;
            default:
                handleEvent((ByteBuf) msg);
                logger.info("handle event");
                break;
        }

    }

    private void handleEvent(ByteBuf buf) {
        Event event = new Event();
        buf.readUnsignedMediumLE();
        buf.readUnsignedByte();
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        event.read(data);
    }

    private void handleError() {

    }
}
