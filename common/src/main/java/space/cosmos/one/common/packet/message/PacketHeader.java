package space.cosmos.one.common.packet.message;

public class PacketHeader {
    public static final byte PACKET_OK = (byte) 0x00;
    public static final byte PACKET_ERR = (byte) 0xff;
    public static final byte PACKET_EOF = (byte) 0xfe;
}
