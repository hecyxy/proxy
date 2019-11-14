package space.cosmos.one.mysql.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.mysql.codec.message.CsGreeting;
import space.cosmos.one.mysql.constant.RemotingType;
import space.cosmos.one.mysql.util.BufferUtils;

import java.nio.charset.Charset;

import static space.cosmos.one.mysql.codec.message.CapabilityFlags.CLIENT_PLUGIN_AUTH;
import static space.cosmos.one.mysql.codec.message.CapabilityFlags.CLIENT_SECURE_CONNECTION;


public class HandshakeParser extends MysqlParser<CsGreeting> {
    private static final Logger logger = LoggerFactory.getLogger(HandshakeParser.class);
    private ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;

    public HandshakeParser(ByteBuf in) {
        super(in);
    }

    public CsGreeting decode() {
        String pluginName = "";
        ByteBuf seed = null;
        ByteBuf buffer = null;
        int serverCapabilities;
        try {
            buffer = allocator.heapBuffer(20);
            seed = allocator.heapBuffer(20);
            short protoVersion = packet.readUnsignedByte();
            BufferUtils.readNullString(packet, buffer);

            int threadId = packet.readIntLE();
            logger.debug("mysql server version is {},threadId: {}", buffer.toString(Charset.forName("ASCII")), threadId);
            if (protoVersion > 9) {
                packet.readBytes(seed, 8);
                packet.skipBytes(1);
            } else {
                BufferUtils.readNullString(packet, seed);
            }
            serverCapabilities = packet.readUnsignedShortLE();//低两位

            System.out.println("capablilities " + serverCapabilities);
            if (packet.isReadable()) {//如果还有可读数据
                int serverCharsetIndex = packet.readUnsignedByte();
                int serverStatus = packet.readUnsignedShortLE();

                serverCapabilities |= packet.readUnsignedShortLE() << 16;//读取高两位


                short authPluginDataLength = 0;
                if ((serverCapabilities & CLIENT_PLUGIN_AUTH) != 0) {
                    authPluginDataLength = packet.readUnsignedByte();
                } else {
                    // read filler ([00])
                    packet.skipBytes(1);
                }
                packet.skipBytes(10);//忽略10个长度的保留字符00


                if ((serverCapabilities & CLIENT_SECURE_CONNECTION) != 0) {
                    buffer.clear();
                    // read string[$len] auth-plugin-data-part-2 ($len=MAX(13, length of auth-plugin-data - 8))
                    BufferUtils.readNullString(packet, buffer);
                    seed.writeBytes(buffer);

                }


                if ((serverCapabilities & CLIENT_PLUGIN_AUTH) != 0) {
                    buffer.clear();
                    BufferUtils.readNullString(packet, buffer);
                    pluginName = buffer.toString(Charset.forName("utf-8"));
                }

            }
            return new CsGreeting(serverCapabilities, pluginName, ByteBufUtil.getBytes(seed));
        } catch (Exception e) {
            logger.warn("parse error", e);
            return null;
        } finally {
            if (seed != null) {
                seed.release();
            }
            if (buffer != null) {
                buffer.release();
            }
        }
    }
}
