package space.cosmos.one.common.packet.binlog;

import space.cosmos.one.common.packet.MysqlMessage;
import space.cosmos.one.common.packet.binlog.decoder.DecoderConfig;
import space.cosmos.one.common.util.BufferUtils;

import java.util.BitSet;

public class TableMapEventData implements EventData {

    private long tableId;
    private String database;
    private String table;
    // column type
    private byte[] columnTypes;
    // column length
    private int[] columnMetadata;
    // 可为NULL的row bit位图
    private BitSet columnNullability;

    public long getTableId() {
        return tableId;
    }

    public String getDatabase() {
        return database;
    }

    public String getTable() {
        return table;
    }

    public byte[] getColumnTypes() {
        return columnTypes;
    }

    public int[] getColumnMetadata() {
        return columnMetadata;
    }

    public BitSet getColumnNullability() {
        return columnNullability;
    }

    public void read(byte[] data) {
        MysqlMessage message = new MysqlMessage(data);

        tableId = message.readLong(6);
        message.skip(3); // 2 bytes reserved for future use + 1 for the length of database name
        database = message.readStringWithNull();
        message.skip(1); //table name
        table = message.readStringWithNull();
        int numberOfColumns = (int) message.readLength();
        columnTypes = message.readBytes(numberOfColumns);
        message.readLength(); // metadata length
        columnMetadata = readMetadata(message, columnTypes);
        columnNullability = message.readBitSet(numberOfColumns, true);
        // 自身注册进tableMap
        DecoderConfig.putTableMap(tableId, this);
    }


    private int[] readMetadata(MysqlMessage mm, byte[] columnTypes) {
        int[] metadata = new int[columnTypes.length];
        for (int i = 0; i < columnTypes.length; i++) {
            switch (ColumnType.byCode(columnTypes[i] & 0xFF)) {
                case FLOAT:
                case DOUBLE:
                case BLOB:
                case GEOMETRY:
                    metadata[i] = mm.read();
                    break;
                case BIT:
                case VARCHAR:
                case NEWDECIMAL:
                    metadata[i] = mm.readUnsignedIntLE();
                    break;
                case SET:
                case ENUM:
                case STRING:
                    metadata[i] = BufferUtils.bigEndianInteger(mm.readBytes(2), 0, 2);
                    break;
                case TIME_V2:
                case DATETIME_V2:
                case TIMESTAMP_V2:
                    metadata[i] = mm.read();
                    break;
                default:
                    metadata[i] = 0;
            }
        }
        return metadata;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("TableMapEventData");
        sb.append("{tableId=").append(tableId);
        sb.append(", database='").append(database).append('\'');
        sb.append(", table='").append(table).append('\'');
        sb.append(", columnTypes=").append(columnTypes == null ? "null" : "");
        for (int i = 0; columnTypes != null && i < columnTypes.length; ++i) {
            sb.append(i == 0 ? "" : ", ").append(columnTypes[i]);
        }
        sb.append(", columnMetadata=").append(columnMetadata == null ? "null" : "");
        for (int i = 0; columnMetadata != null && i < columnMetadata.length; ++i) {
            sb.append(i == 0 ? "" : ", ").append(columnMetadata[i]);
        }
        sb.append(", columnNullability=").append(columnNullability);
        sb.append('}');
        return sb.toString();
    }
}
