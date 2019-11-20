package space.cosmos.one.common.packet.binlog;

import space.cosmos.one.common.packet.MysqlMessage;

public class XidEventData implements EventData {
    private long xid;

    public void read(byte[] data) {
        MysqlMessage mm = new MysqlMessage(data);
        xid = mm.readLong(8);
    }

    public long getXid() {
        return xid;
    }

    public void setXid(long xid) {
        this.xid = xid;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("XidEventData");
        sb.append("{xid=").append(xid);
        sb.append('}');
        return sb.toString();
    }
}
