package space.cosmos.one.mysql.handler;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteOrder;
import java.util.List;

import static space.cosmos.one.mysql.util.BufferUtils.isReadable;

/**
 * @Description 两种方式解决粘包问题
 */
public class MysqlDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(MysqlDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (isReadable(in)) {
            logger.info("readable bytes,{}", in.readableBytes());
            ByteBuf three = in.readBytes(3);
            byte two = in.readByte();
            //这里有一处修改
            int length = three.getUnsignedMediumLE(three.readerIndex());
            ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(length + 4);
            buf.writeBytes(three);
            buf.writeByte(two);
            buf.writeBytes(in.readBytes(length));
            buf.resetReaderIndex();
            out.add(buf);
        }
    }
}

// the follow method is also correct
//public class MysqlDecoder extends LengthFieldBasedFrameDecoder {
//    private static final int MAX_PACKET_LENGTH = 8192;
//
//    public MysqlDecoder() {
//        super(ByteOrder.LITTLE_ENDIAN, MAX_PACKET_LENGTH, 0, 3, 1, 0, true);
//    }
//
//    @Override
//    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
//        return super.decode(ctx, in);
//    }
//}

