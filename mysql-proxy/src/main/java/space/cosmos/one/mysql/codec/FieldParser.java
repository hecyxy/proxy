package space.cosmos.one.mysql.codec;

import io.netty.buffer.ByteBuf;
import space.cosmos.one.mysql.codec.message.FieldFlag;
import space.cosmos.one.mysql.codec.message.FieldType;
import space.cosmos.one.mysql.codec.message.MysqlField;
import space.cosmos.one.mysql.codec.message.ResultSetFieldMessage;
import space.cosmos.one.mysql.util.BufferUtils;

import java.util.EnumSet;
import java.util.Set;

public class FieldParser extends MysqlParser<ResultSetFieldMessage> {
    private int index;

    public FieldParser(ByteBuf packet, int index) {
        super(packet);
        this.index = index;
    }

    @Override
    public ResultSetFieldMessage decodeBody0() {
        String catalogName = BufferUtils.readEncodedLenString(packet);
        String schemaName = BufferUtils.readEncodedLenString(packet);
        String tableLabel = BufferUtils.readEncodedLenString(packet);
        String tableName = BufferUtils.readEncodedLenString(packet);
        String columnLabel = BufferUtils.readEncodedLenString(packet);
        String columnName = BufferUtils.readEncodedLenString(packet);

        packet.skipBytes(1);//length of fixed-length fields [0c]
        int characterSetIndex = packet.readUnsignedShortLE();
        MysqlCharacterSet charSet = MysqlCharacterSet.findById(characterSetIndex);

        int columnLength = (int) packet.readUnsignedIntLE();
        int columnTypeId = packet.readUnsignedByte();
        FieldType columnType = FieldType.valueOf(columnTypeId);

        Set<FieldFlag> flags = toEnumSet(packet.readUnsignedShortLE());
        int colDecimals = packet.readUnsignedByte();
        packet.skipBytes(2);//filler [00] [00]

        MysqlField field = new MysqlField(index, catalogName, schemaName, tableLabel, tableName, columnType, columnLabel,
                columnName, columnLength, flags, colDecimals);
        ResultSetFieldMessage response = new ResultSetFieldMessage(packetLen, packetNo, field);
        return response;
    }

    private EnumSet<FieldFlag> toEnumSet(long vector) {
        EnumSet<FieldFlag> set = EnumSet.noneOf(FieldFlag.class);
        for (FieldFlag e : FieldFlag.values()) {
            if ((e.code() & vector) > 0) {
                set.add(e);
            }
        }
        return set;
    }
}
