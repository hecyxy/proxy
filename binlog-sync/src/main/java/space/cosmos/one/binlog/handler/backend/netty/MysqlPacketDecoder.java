package space.cosmos.one.binlog.handler.backend.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteOrder;

public class MysqlPacketDecoder extends LengthFieldBasedFrameDecoder {
    private static final Logger logger = LoggerFactory.getLogger(MysqlPacketDecoder.class);

    private static final int maxSize = 16 * 1024 * 1024;


    public MysqlPacketDecoder() {
        super(ByteOrder.LITTLE_ENDIAN, maxSize, 0, 3, 1, 0, true);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        return super.decode(ctx, in);
    }
}
