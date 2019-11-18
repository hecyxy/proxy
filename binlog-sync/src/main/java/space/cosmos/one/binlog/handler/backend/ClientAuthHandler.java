package space.cosmos.one.binlog.handler.backend;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.binlog.handler.ConnectionState;

class ClientAuthHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ClientAuthHandler.class);


    private ConnectionState state = ConnectionState.NOT_AUTH;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        switch (state) {
            case NOT_AUTH:
                break;
            case AUTH_SUC:
                break;
            case RS_FIELD_COUNT:
                break;
            case RS_FIELD:
                break;
            case RS_EOF:
                break;
            case RS_ROW:
                break;
            default:
                break;
        }
    }
}
