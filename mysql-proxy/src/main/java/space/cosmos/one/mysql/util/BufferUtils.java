package space.cosmos.one.mysql.util;

import io.netty.buffer.ByteBuf;
import io.netty.util.ByteProcessor;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class BufferUtils {
    public static final int RESPONSE_ERROR = 0xFE;
    public static final int RESPONSE_OK = 0x00;
    public static final int RESPONSE_EOF = 0xFE;


    public static void readNullString(ByteBuf from, ByteBuf to) {
        int len = from.forEachByte(ByteProcessor.FIND_NUL) - from.readerIndex();
        to.ensureWritable(len);
        from.readBytes(to, len);
        from.skipBytes(1);
    }

    public static String readRestPacket(ByteBuf buf) {
        return buf.readBytes(buf.readableBytes()).toString(StandardCharsets.UTF_8);
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
}
