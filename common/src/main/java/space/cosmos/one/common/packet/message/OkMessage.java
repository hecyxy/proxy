package space.cosmos.one.common.packet.message;

public class OkMessage {
    private long affectedRows;
    private long lastInsertId;
    public OkMessage(long affectedRows, long lastInsertId){
        this.affectedRows = affectedRows;
        this.lastInsertId = lastInsertId;
    }

    public long getAffectedRows() {
        return affectedRows;
    }

    public long getLastInsertId() {
        return lastInsertId;
    }

    @Override
    public String toString(){
        return String.format("ok msg,affectRows %s;lastInsertId: %s",affectedRows,lastInsertId);
    }
}
