package space.cosmos.one.common.packet.binlog;

import space.cosmos.one.common.packet.MysqlMessage;

public class RotateEventData implements EventData {
    private long binlogPosition;
    private String binlogFileName;

    public void read(byte[] data) {
        MysqlMessage mysqlMessage = new MysqlMessage(data);
        binlogPosition = mysqlMessage.readLong();
        binlogFileName = mysqlMessage.readString();
    }

    public long getBinlogPosition() {
        return binlogPosition;
    }

    public void setBinlogPosition(long binlogPosition) {
        this.binlogPosition = binlogPosition;
    }

    public String getBinlogFileName() {
        return binlogFileName;
    }

    public void setBinlogFileName(String binlogFileName) {
        this.binlogFileName = binlogFileName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("RotateEventData");
        sb.append("{binlogFilename='").append(binlogFileName).append('\'');
        sb.append(", binlogPosition=").append(binlogPosition);
        sb.append('}');
        return sb.toString();
    }
}
