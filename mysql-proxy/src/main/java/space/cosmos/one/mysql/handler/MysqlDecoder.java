package space.cosmos.one.mysql.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteOrder;
import java.util.List;

import static space.cosmos.one.mysql.util.BufferUtils.isReadable;

//public class MysqlDecoder extends ByteToMessageDecoder {
//    private static final Logger logger = LoggerFactory.getLogger(MysqlDecoder.class);
//
//    @Override
//    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//        while (isReadable(in)) {
////            out.add(in);
////            int total = in.readableBytes();
////            int len = in.readUnsignedMediumLE();
////            int packetNo = in.readUnsignedByte();
////            out.add(len);
////            out.add(packetNo);
////            out.add(in.readBytes(len - 1));
//            in.resetReaderIndex();
//            out.add(in);
//            return;
//        }
//    }
//}

public class MysqlDecoder extends LengthFieldBasedFrameDecoder {
    private static final int MAX_PACKET_LENGTH = 8192 * 2;

    public MysqlDecoder() {
        super(ByteOrder.LITTLE_ENDIAN, MAX_PACKET_LENGTH, 0, 3, 1, 0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        return super.decode(ctx, in);
    }
}

