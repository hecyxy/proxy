package space.cosmos.one.binlog.handler.backend.result;

public class BinlogContext {

    private String binlogFileName;

    private long binlogPosition;

    private String executedGtidSet;

    private GtidSet gtidSet;

    private ChecksumType checksumType;


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
