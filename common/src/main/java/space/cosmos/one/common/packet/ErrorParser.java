package space.cosmos.one.common.packet;

import io.netty.buffer.ByteBuf;
import space.cosmos.one.common.packet.message.ErrorMessage;
import space.cosmos.one.common.util.BufferUtils;

public class ErrorParser extends MysqlParser<ErrorMessage> {
    public ErrorParser(ByteBuf packet) {
        super(packet);
    }

    @Override
    public ErrorMessage decodeBody0() {
        packet.skipBytes(1);//0xff

        int errorCode = packet.readUnsignedShortLE();
        packet.skipBytes(1);//忽略sql_state_marker
        String sqlState = BufferUtils.readString(packet, 5);
        byte[] errBytes = new byte[this.packetLen - 1 - 2 - 6];
        packet.readBytes(errBytes);
        String errMsg = BufferUtils.toString(errBytes);
        return new ErrorMessage(packetLen, packetNo, errorCode, sqlState, errMsg);
    }
}
