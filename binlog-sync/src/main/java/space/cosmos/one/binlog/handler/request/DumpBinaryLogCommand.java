package space.cosmos.one.binlog.handler.request;

public class DumpBinaryLogCommand extends ClientRequest {
    private long serverId;
    private String binlogFilename;
    private long binlogPosition;


    public DumpBinaryLogCommand(long serverId, String binlogFilename, long binlogPosition) {
        super(1);
        this.serverId = serverId;
        this.binlogFilename = binlogFilename;
        this.binlogPosition = binlogPosition;
    }

    protected String getPacketInfo() {
        return "DumpBinaryLogCommand";
    }

    public int calcPacketSize() {
        return 1 + 4 + 2 + 4 + binlogFilename.length();
    }
}
