package space.cosmos.one.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.ByteBuffer;
import java.util.BitSet;
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

    public byte[] readBytes(int length) {
        byte[] ab = new byte[length];
        System.arraycopy(data, position, ab, 0, length);
        position += length;
        return ab;
    }

    public byte[] readBytes() {
        if (position >= length) {
            return EMPTY_BYTES;
        }
        byte[] ab = new byte[length - position];
        System.arraycopy(data, position, ab, 0, ab.length);
        position = length;
        return ab;
    }

    public void skip(int i) {
        position += i;
    }

    public long readLong() {
        final byte[] b = this.data;
        long l = (long) (b[position++] & 0xff);
        l |= (long) (b[position++] & 0xff) << 8;
        l |= (long) (b[position++] & 0xff) << 16;
        l |= (long) (b[position++] & 0xff) << 24;
        l |= (long) (b[position++] & 0xff) << 32;
        l |= (long) (b[position++] & 0xff) << 40;
        l |= (long) (b[position++] & 0xff) << 48;
        l |= (long) (b[position++] & 0xff) << 56;
        return l;
    }

    public long readLong(int length) {
        final byte[] b = this.data;
        long l = 0;
        for (int i = 0; i < length; i++) {
            l |= ((long) (b[position++] & 0xff) << (i << 3));
        }
        return l;
    }

    /**
     * 读到null结尾的byte[]
     *
     * @return
     */
    public byte[] readBytesEndWithNull() {
        byte[] b = this.data;
        if (position > length) {
            return EMPTY_BYTES;
        }
        int offset = -1;
        for (int i = position; i < length; i++) {
            if (b[i] == 0) {
                offset = i;
                break;
            }
        }
        switch (offset) {
            case -1:
                byte[] ab1 = new byte[length - position];
                System.arraycopy(b, position, ab1, 0, ab1.length);
                position = length;
                return ab1;
            case 0:
                position++;
                return EMPTY_BYTES;
            default:
                byte[] ab2 = new byte[offset];
                System.arraycopy(b, position, ab2, 0, ab2.length);
                position = offset + 1;
                return ab2;
        }
    }

    public long readLength() {
        int length = data[position++] & 0xff;
        switch (length) {
            case 251:
                return NULL_LENGTH;
            case 252:
                return readUnsignedIntLE();
            case 253:
                return readUnsignedMediumLE();
            case 254:
                return readUnsignedLongLE();
            default:
                return length;
        }
    }

    public String readStringWithCrc32() {
        if (position >= length - 4) {
            return null;
        }
        String s = new String(data, position, length - position - 4);
        position = length;
        return s;
    }

    public String readStringWithNull() {
        final byte[] b = this.data;
        if (position >= length) {
            return null;
        }
        int offset = -1;
        for (int i = position; i < length; i++) {
            if (b[i] == 0) {
                offset = i;
                break;
            }
        }
        if (offset == -1) {
            String s = new String(b, position, length - position);
            position = length;
            return s;
        }
        if (offset > position) {
            String s = new String(b, position, offset - position);
            position = offset + 1;
            return s;
        } else {
            position++;
            return null;
        }
    }

    public int readUnsignedIntLE() {
        final byte[] b = this.data;
        // 保持二进制补码的一致性 因为byte类型字符是8bit的  而int为32bit 会自动补齐高位1
        // 所以与上0xFF之后可以保持高位一致性 当byte要转化为int的时候，高的24位必然会补1，
        // 这样，其二进制补码其实已经不一致了，&0xff可以将高的24位置为0，低8位保持原样，这样做的目的就是为了保证二进制数据的一致性。

        int i = b[position++] & 0xff;
        i |= (b[position++] & 0xff) << 8;
        ByteBufAllocator.DEFAULT.buffer().getUnsignedIntLE(0);
        return i;
    }

    public int readUnsignedMediumLE() {
        final byte[] b = this.data;
        int i = b[position++] & 0xff;
        i |= (b[position++] & 0xff) << 8;
        i |= (b[position++] & 0xff) << 16;
        return i;
    }

    public long readUnsignedLongLE() {
        final byte[] b = this.data;
        long l = (long) (b[position++] & 0xff);
        l |= (long) (b[position++] & 0xff) << 8;
        l |= (long) (b[position++] & 0xff) << 16;
        l |= (long) (b[position++] & 0xff) << 24;
        return l;
    }

    public BitSet readBitSet(int length, boolean bigEndian) {
        // according to MySQL internals the amount of storage required for N columns is INT((N+7)/8) bytes
        byte[] bytes = readBytes((length + 7) >> 3);
        bytes = bigEndian ? bytes : reverse(bytes);
        BitSet result = new BitSet();
        for (int i = 0; i < length; i++) {
            if ((bytes[i >> 3] & (1 << (i % 8))) != 0) {
                result.set(i);
            }
        }
        return result;
    }

    private static byte[] reverse(byte[] bytes) {
        for (int i = 0, length = bytes.length >> 1; i < length; i++) {
            int j = bytes.length - 1 - i;
            byte t = bytes[i];
            bytes[i] = bytes[j];
            bytes[j] = t;
        }
        return bytes;
    }

    public int readInteger(int length) {
        final byte[] b = this.data;
        int l = 0;
        for (int i = 0; i < length; ++i) {
            l |= ((b[position++] & 0xff) << (i << 3));
        }
        return l;
    }

    public String readString() {
        if (position >= length) {
            return null;
        }
        String s = new String(data, position, length - position);
        position = length;
        return s;
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

    // 是否已经读取完毕
    public long available() {
        return length - position;
    }
}
