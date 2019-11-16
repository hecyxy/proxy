package space.cosmos.one.mysql.codec;

import io.netty.buffer.ByteBuf;
import space.cosmos.one.mysql.codec.message.ResultSetRowMessage;
import space.cosmos.one.mysql.util.BufferUtils;

public class RowParser extends MysqlParser<ResultSetRowMessage> {
    private int colCount;

    public RowParser(ByteBuf packet, int colCount) {
        super(packet);
        this.colCount = colCount;
    }

    @Override
    public ResultSetRowMessage decodeBody0() {
        String[] row = new String[colCount];
        for (int i = 0; i < colCount; i++) {
            String value = null;
            short firstByte = packet.getUnsignedByte(packet.readerIndex());
            if (firstByte != 0xFB) {
                value = BufferUtils.readEncodedLenString(packet);
            } else {
                packet.skipBytes(1);
            }
            row[i] = value;
        }
        return new ResultSetRowMessage(packetLen, packetNo, row);
    }
}
