package space.cosmos.one.mysql.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteOrder;

public class BackendMysqlDecoder extends LengthFieldBasedFrameDecoder {
    private static final int MAX_PACKET_LENGTH = 8192;

    public BackendMysqlDecoder() {
        super(ByteOrder.LITTLE_ENDIAN, MAX_PACKET_LENGTH, 0, 4, 1, 0, true);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        return super.decode(ctx, in);
    }
}
