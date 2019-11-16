package space.cosmos.one.mysql.codec;

import io.netty.buffer.ByteBuf;
import space.cosmos.one.mysql.codec.message.OkMessage;
import space.cosmos.one.mysql.util.BufferUtils;

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
