package space.cosmos.one.binlog.handler.request;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import space.cosmos.one.common.util.BufferUtils;

public class DumpBinaryLogCommand extends ClientRequest {
    private long serverId;
    private String binlogFilename;
    private long binlogPosition;

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public String getBinlogFilename() {
        return binlogFilename;
    }

    public void setBinlogFilename(String binlogFilename) {
        this.binlogFilename = binlogFilename;
    }

    public long getBinlogPosition() {
        return binlogPosition;
    }

    public void setBinlogPosition(long binlogPosition) {
        this.binlogPosition = binlogPosition;
    }

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

    @Override
    public String toString() {
        return "serverId:" + serverId
                + ",binlogFileName:" + binlogFilename
                + ",binlogPosition:" + binlogPosition;
    }
}
