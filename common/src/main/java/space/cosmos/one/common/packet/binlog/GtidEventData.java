package space.cosmos.one.common.packet.binlog;

public class GtidEventData implements EventData {

    private String gtid;

    private byte flags;

    public void read(byte[] data) {

    }

    private String byteArr2Hex(byte[] a, int offset, int len) {
        StringBuilder sb = new StringBuilder();
        for (int idx = offset; idx < (offset + len) && idx < a.length; idx++) {
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
