package space.cosmos.one.common.packet.binlog;

import space.cosmos.one.common.packet.MysqlMessage;

public class FormatDescriptionEventData implements EventData{

    private int binlogVersion;
    private String serverVersion;
    private int headerLength;
    private ChecksumType checksumType;

    public void read(byte[] data) {
        MysqlMessage mm = new MysqlMessage(data);
        binlogVersion = mm.readUnsignedIntLE();
        serverVersion = new String(mm.readBytes(50)).trim();
        mm.skip(4);
        headerLength = mm.read();
        checksumType = ChecksumType.NONE;
    }

    public int getBinlogVersion() {
        return binlogVersion;
    }

    public void setBinlogVersion(int binlogVersion) {
        this.binlogVersion = binlogVersion;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public int getHeaderLength() {
        return headerLength;
    }

    public void setHeaderLength(int headerLength) {
        this.headerLength = headerLength;
    }

    public ChecksumType getChecksumType() {
        return checksumType;
    }

    public void setChecksumType(ChecksumType checksumType) {
        this.checksumType = checksumType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("FormatDescriptionEventData");
        sb.append("{binlogVersion=").append(binlogVersion);
        sb.append(", serverVersion='").append(serverVersion).append('\'');
        sb.append(", headerLength=").append(headerLength);
        sb.append(", checksumType=").append(checksumType);
        sb.append('}');
        return sb.toString();
    }
}