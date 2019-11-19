package space.cosmos.one.binlog.handler.backend.result;


public enum ChecksumType {
    NONE(0), CRC32(4);

    private int length;

    ChecksumType(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }
}
