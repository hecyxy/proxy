package space.cosmos.one.common.packet.binlog;

import space.cosmos.one.common.packet.MysqlMessage;

public class GtidEventData implements EventData {

    private String gtid;

    private byte flags;

    public void read(byte[] data) {
        MysqlMessage msg = new MysqlMessage(data);
        flags = msg.read();
        byte[] sid = msg.readBytes(16);
        long go = msg.readLong();
        gtid = byteArr2Hex(sid, 0, 4) + "-" +
                byteArr2Hex(sid, 4, 2) + "-" +
                byteArr2Hex(sid, 6, 2) + "-" +
                byteArr2Hex(sid, 8, 2) + "-" +
                byteArr2Hex(sid, 10, 6) + ":" +
                String.format("%d", go);
    }

    private String byteArr2Hex(byte[] a, int offset, int len) {
        StringBuilder sb = new StringBuilder();
        for (int idx = offset; idx < (offset + len) && idx < a.length; idx++) {
            // 格式控制: 以十六进制输出,2为指定的输出字段的宽度.如果位数小于2,则左端补0
            sb.append(String.format("%02x", a[idx] & 0xff));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "GtidEventData" +
                "[{flags=" + flags + ",gtid='" + gtid + "\'" +
                '}';
    }

}
