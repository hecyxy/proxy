package space.cosmos.one.common.packet.binlog;

import space.cosmos.one.common.packet.MysqlMessage;
import space.cosmos.one.common.packet.binlog.decoder.DecoderConfig;


public class QueryEventData implements EventData {
    private long threadId;
    private long executionTime;
    private int errorCode;
    private byte[] statusVar;
    private String database;
    private String sql;

    public void read(byte[] data) {
        MysqlMessage mm = new MysqlMessage(data);
        threadId = mm.readUnsignedLongLE();
        executionTime = mm.readUnsignedLongLE();
        mm.skip(1); // length of the name of the database
        errorCode = mm.readUnsignedIntLE();
        int statusVarLength = mm.readUnsignedIntLE(); // status variables block
        statusVar = mm.readBytes(statusVarLength);
        database = mm.readStringWithNull();
        if (DecoderConfig.checksumType == ChecksumType.CRC32) {
            sql = mm.readStringWithCrc32();
        } else {
            sql = mm.readString();
        }
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("QueryEventData");
        sb.append("{threadId=").append(threadId);
        sb.append(", executionTime=").append(executionTime);
        sb.append(", errorCode=").append(errorCode);
        sb.append(", database='").append(database).append('\'');
        sb.append(", sql='").append(sql).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
