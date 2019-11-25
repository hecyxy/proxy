package space.cosmos.one.binlog.handler.backend.result;

import space.cosmos.one.binlog.handler.backend.result.handler.ResultSet;
import space.cosmos.one.common.packet.binlog.ChecksumType;

public class BinlogContext {

    private String binlogFileName;

    private long binlogPosition;

    private String executedGtidSet;

    private GtidSet gtidSet;

    private ChecksumType checksumType;

    public void handlePositionResult(ResultSet resultSet) {
        String[] row = resultSet.getRows().get(0);
        binlogFileName = row[0];
        binlogPosition = Long.parseLong(row[1]);
        // 低版本兼容
        if (row.length > 4) {
            executedGtidSet = row[4].replace("\n", "");
        }
        checksumType = ChecksumType.NONE;
    }


    public void useGtidSet() {
        gtidSet = new GtidSet(executedGtidSet);
    }

    public String getBinlogFileName() {
        return binlogFileName;
    }

    public void setBinlogFileName(String binlogFileName) {
        this.binlogFileName = binlogFileName;
    }

    public long getBinlogPosition() {
        return binlogPosition;
    }

    public void setBinlogPosition(long binlogPosition) {
        this.binlogPosition = binlogPosition;
    }

    public String getExecutedGtidSet() {
        return executedGtidSet;
    }

    public void setExecutedGtidSet(String executedGtidSet) {
        this.executedGtidSet = executedGtidSet;
    }

    public GtidSet getGtidSet() {
        return gtidSet;
    }

    public void setGtidSet(GtidSet gtidSet) {
        this.gtidSet = gtidSet;
    }

    public ChecksumType getChecksumType() {
        return checksumType;
    }

    public void setChecksumType(ChecksumType checksumType) {
        this.checksumType = checksumType;
    }
}
