package space.cosmos.one.common.packet;

import java.util.Calendar;

public class MysqlMessage {

    public static final long NULL_LENGTH = -1;
    private static final byte[] EMPTY_BYTES = new byte[0];

    private final byte[] data;
    private final int length;
    private int position;

    private static final ThreadLocal<Calendar> localCalendar = new ThreadLocal<Calendar>();

    public MysqlMessage(byte[] data) {
        this.data = data;
        this.length = data.length;
        this.position = 0;
    }


    public byte read() {
        return data[position++];
    }

    public static long getNullLength() {
        return NULL_LENGTH;
    }

    public static byte[] getEmptyBytes() {
        return EMPTY_BYTES;
    }

    public byte[] getData() {
        return data;
    }

    public int getLength() {
        return length;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
