package space.cosmos.one.common.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ByteProcessor;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class BufferUtils {

    private final static ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;

    public static void readNullString(ByteBuf from, ByteBuf to) {
        int len = from.forEachByte(ByteProcessor.FIND_NUL) - from.readerIndex();
        to.ensureWritable(len);
        from.readBytes(to, len);
        from.skipBytes(1);
    }

    public static String readRestPacket(ByteBuf buf) {
        return buf.readBytes(buf.readableBytes()).toString(StandardCharsets.US_ASCII);
    }

    public static boolean isReadable(ByteBuf in) {
        if (in.readableBytes() < 4) {
            return false;
        }
        int packetLength = in.getUnsignedMediumLE(in.readerIndex());
        if (in.readableBytes() < 4 + packetLength) {
            return false;
        }
        return true;
    }

    public static long readEncodedLenInt(ByteBuf buffer) {
        int firstByte = buffer.readUnsignedByte();
        return readEncodedLenInt(buffer, firstByte);
    }

    public static long readEncodedLenInt(ByteBuf buffer, int firstByte) {
        if (firstByte < 251) {
            return firstByte;
        } else if (firstByte == 0xfc) {
            return buffer.readUnsignedShortLE(); //2个字节
        } else if (firstByte == 0xfd) {
            return buffer.readUnsignedMediumLE();//3个字节
        } else if (firstByte == 0xfe) {
            return buffer.readLongLE();
        }
        return -1;
    }

    public static String readString(ByteBuf in, int len) {
        byte[] data = new byte[len];
        in.readBytes(data);
        return toString(data);
    }

    public static String toString(byte[] b) {
        return toString(b, "UTF-8");
    }

    public static String toString(byte[] b, String encoding) {
        try {
            return new String(b, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("not support" + encoding);
        }
    }

    public static String readEncodedLenString(ByteBuf buf) {
        int len = (int) readEncodedLenInt(buf);
        byte[] data = new byte[len];
        buf.readBytes(data);
        try {
            return new String(data, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("not support utf8");
        }
    }

    public static boolean isEOFPacket(ByteBuf in) {
        int len = in.getUnsignedMediumLE(in.readerIndex());
        return (len <= 5) && (in.getUnsignedByte(in.readerIndex() + 4) == 0xFE);
    }

    public static byte[] toBytes(String str) {
        return toBytes(str, "UTF-8");
    }

    public static byte[] toBytes(String str, String encoding) {
        try {
            return str.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("not support " + encoding);
        }
    }

    public static int bigEndianInteger(byte[] bytes, int offset, int length) {
        int result = 0;
        for (int i = offset; i < (offset + length); i++) {
            byte b = bytes[i];
            result = (result << 8) | (b >= 0 ? (int) b : (b + 256));
        }
        return result;
    }

    public static ByteBuf wrapString(String text) {
        ByteBuf data = allocator.buffer();
        data.writeCharSequence(text, Charset.forName("UTF-8"));
        return data;
    }

    public static final void writeUB3(ByteBuf buffer, int i) {
        buffer.writeByte((byte) (i & 0xff));
        buffer.writeByte((byte) (i >>> 8));
        buffer.writeByte((byte) (i >>> 16));
    }
    public static void writeInteger(ByteBuf buffer, int value, int length) {
        for (int i = 0; i < length; i++) {
            buffer.writeByte(0x000000FF & (value >>> (i << 3)));
        }
    }
    public static final void writeUB4(ByteBuf buffer, long l) {
        buffer.writeByte((byte) (l & 0xff));
        buffer.writeByte((byte) (l >>> 8));
        buffer.writeByte((byte) (l >>> 16));
        buffer.writeByte((byte) (l >>> 24));
    }


}
