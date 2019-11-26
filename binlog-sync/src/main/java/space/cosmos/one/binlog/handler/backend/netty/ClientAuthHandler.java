package space.cosmos.one.binlog.handler.backend.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.binlog.handler.ConnectionState;
import space.cosmos.one.binlog.handler.backend.result.handler.PositionResultHandler;
import space.cosmos.one.binlog.handler.factory.BackendConnection;
import space.cosmos.one.binlog.handler.request.AuthRequest;
import space.cosmos.one.binlog.handler.request.CommandRequest;
import space.cosmos.one.binlog.util.AuthUtil;
import space.cosmos.one.binlog.util.SystemConfig;
import space.cosmos.one.common.packet.HandshakeParser;
import space.cosmos.one.common.packet.message.Command;
import space.cosmos.one.common.packet.message.CsGreeting;
import space.cosmos.one.common.util.BufferUtils;

import static space.cosmos.one.common.packet.message.CapabilityFlags.*;
import static space.cosmos.one.common.packet.message.ResponseType.RESPONSE_ERROR;
import static space.cosmos.one.common.packet.message.ResponseType.RESPONSE_OK;

public class ClientAuthHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ClientAuthHandler.class);


    private ConnectionState state = ConnectionState.NOT_AUTH;

    private BackendConnection connection;

    public ClientAuthHandler(BackendConnection connection) {
        this.connection = connection;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        switch (state) {
            case NOT_AUTH:
                auth(ctx, msg);
                state = ConnectionState.AUTH_SUC;
                break;
            case AUTH_SUC:
                decodeAuthResponse((ByteBuf) msg, ctx);
                break;
            default:
                logger.info("state ", state);
                break;
        }
    }

    private void auth(ChannelHandlerContext ctx, Object msg) {
        HandshakeParser handshake = new HandshakeParser((ByteBuf) msg);
        if (!handshake.decode()) {
            return;
        }
        AuthRequest request = doHandShake(handshake.getBody(), ctx.alloc());
        ctx.writeAndFlush(request);
    }

    private AuthRequest doHandShake(CsGreeting packet, ByteBufAllocator allocator) {
        int clientParam = CLIENT_CONNECT_WITH_DB | CLIENT_PLUGIN_AUTH | CLIENT_LONG_PASSWORD | CLIENT_PROTOCOL_41 | CLIENT_TRANSACTIONS //
                | CLIENT_MULTI_RESULTS | CLIENT_SECURE_CONNECTION; //

        if ((packet.getServerCapabilities() & CLIENT_LONG_FLAG) != 0) {
            // We understand other column flags, as well
            clientParam |= CLIENT_LONG_FLAG;
        }
        if ((packet.getServerCapabilities() & CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA) != 0) {
            clientParam |= CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA;
        }
        if ((packet.getServerCapabilities() & CLIENT_DEPRECATE_EOF) != 0) {
            clientParam |= CLIENT_DEPRECATE_EOF;
        }
	       /* if ((parser.getServerCapabilities() & CLIENT_CONNECT_ATTRS) != 0) {
	            clientParam |= CLIENT_CONNECT_ATTRS;
	        }*/
        if ((packet.getServerCapabilities() & CLIENT_PLUGIN_AUTH) != 0) {
            ByteBuf response = allocator.buffer();
            response.writeIntLE(clientParam);//capability flags, CLIENT_PROTOCOL_41 always set
            response.writeInt((256 * 256 * 256) - 1);//数据包的最大大小
            response.writeByte(33);//字符集
            response.writeBytes(new byte[23]);

            response.writeBytes(SystemConfig.userName.getBytes());//用户名
            response.writeByte(0x00);
            byte[] authResponse = AuthUtil.getPlugin(packet.getPluginName()).process(SystemConfig.pwd.getBytes(), packet.getSeed());

            if ((clientParam & CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA) != 0) {
                // request lenenc-int length of auth-response and string[n] auth-response
                response.writeByte(authResponse.length);
                response.writeBytes(authResponse);
            } else if ((clientParam & CLIENT_SECURE_CONNECTION) != 0) {
                // request 1 byte length of auth-response and string[n] auth-response
                response.writeByte(authResponse.length);
                response.writeBytes(authResponse);
            } else {
                response.writeBytes(authResponse);
                response.writeByte(0x00);
            }
            if ((clientParam & CLIENT_CONNECT_WITH_DB) != 0) {
                response.writeBytes(SystemConfig.dataBase.getBytes());
                response.writeByte(0x00);
            }

            if ((clientParam & CLIENT_PLUGIN_AUTH) != 0) {
                response.writeBytes(BufferUtils.toBytes(packet.getPluginName()));
                response.writeByte(0x00);
            }
            return new AuthRequest(0x01, response);
        }
        return null;
    }

    private void decodeAuthResponse(ByteBuf msg, ChannelHandlerContext ctx) {
        short packType = msg.getUnsignedByte(msg.readerIndex() + 4);
        switch (packType) {
            case RESPONSE_OK:
                logger.info("auth ok ...");
                startDumBinlog(ctx);
                break;
            case RESPONSE_ERROR:
                logger.info("auth error");
                break;
            default:
                logger.info("unknown packet");
                break;
        }
    }

    private void startDumBinlog(ChannelHandlerContext ctx) {
        ctx.pipeline().replace(this, "command adapter", new BackendCommandHandler());
        if (connection.getBinlogContext().getBinlogFileName() == null) {
            connection.setResultSetHandler(new PositionResultHandler(connection));
            ctx.writeAndFlush(new CommandRequest(Command.QUERY, BufferUtils.wrapString("show master status")));
        } else {
            ctx.writeAndFlush(new CommandRequest(Command.QUERY, BufferUtils.wrapString("show global variables like 'gtid_mode'")));
//            connection.setResultSetHander(new GitModeResultHandler(source));
        }
    }
}
