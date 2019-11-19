package space.cosmos.one.common.packet;

import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MysqlParser<T> {
    private static final Logger logger = LoggerFactory.getLogger(MysqlParser.class);
    protected ByteBuf packet;
    private T body;
    protected int packetLen;
    protected int packetNo;
    private int startIndex;

    private boolean decodeHeader = true;

    private boolean initialized = false;

    /**
     * 包的格式:包体长度3B 序号1B 包体
     */
    private void decodeHeader() {
        if (isDecodeHeader()) {
            this.packetLen = packet.readUnsignedMediumLE();
            this.packetNo = packet.readUnsignedByte();
            this.startIndex = packet.readerIndex();
        }
    }

    private void decodeBody() {
        this.body = decodeBody0();
        int readBytes = packet.readerIndex() - startIndex;
        logger.info(String.format("packet len %s,startIndex %s readerIndex %s", packetLen, startIndex, packet.readerIndex()));
        if (readBytes < packetLen) {
            packet.skipBytes(packetLen - readBytes);
        }

    }


    public abstract T decodeBody0();

    public MysqlParser(ByteBuf packet) {
        this.packet = packet;
    }

    boolean isDecodeHeader() {
        return decodeHeader;
    }

    public T getBody() {
        return body;
    }

    public boolean decode() {
        decodeHeader();
        decodeBody();
        return true;
    }
}
