package space.cosmos.one.common.packet.binlog;

import space.cosmos.one.common.packet.MysqlMessage;

public class RowsQueryEventData implements EventData {
    private String query;

    public void read(byte[] data) {
        MysqlMessage mm = new MysqlMessage(data);
        int len = mm.read();
        query = new String(mm.readBytes(len));
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String toString() {
        return "RowsQueryEventData" +
                "{query='" + query + '\'' +
                '}';
    }
}
