package space.cosmos.one.common.packet;

import io.netty.buffer.ByteBuf;
import space.cosmos.one.common.packet.message.OkMessage;
import space.cosmos.one.common.util.BufferUtils;

public class OkParser extends MysqlParser<OkMessage> {
    public OkParser(ByteBuf packet) {
        super(packet);
    }

    @Override
    public OkMessage decodeBody0() {
        //0x00表示ok
        packet.skipBytes(1);
        long affectNum = BufferUtils.readEncodedLenInt(packet);
        long lastInsertId = BufferUtils.readEncodedLenInt(packet);
        return new OkMessage(affectNum, lastInsertId);
    }
}
