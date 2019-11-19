package space.cosmos.one.common.packet.message;

public abstract class ServerMessage {
    private int packetLength;
    private int packetNumber;

    public ServerMessage(int packetLength, int packetNumber) {
        this.packetLength = packetLength;
        this.packetNumber = packetNumber;
    }

    public int getPacketLength() {
        return packetLength;
    }

    public int getPacketNumber() {
        return packetNumber;
    }
}
