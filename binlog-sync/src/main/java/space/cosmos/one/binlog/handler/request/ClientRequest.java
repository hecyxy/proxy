package space.cosmos.one.binlog.handler.request;

public abstract class ClientRequest {

    private final int sequenceNo;

    public ClientRequest(int sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }
}
