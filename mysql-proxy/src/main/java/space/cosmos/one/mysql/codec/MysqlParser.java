package space.cosmos.one.mysql.codec;

import io.netty.buffer.ByteBuf;
import space.cosmos.one.mysql.constant.RemotingType;

public abstract class MysqlParser<T> {
    protected ByteBuf packet;
    private T body;

    public abstract T decode();
    public MysqlParser(ByteBuf packet) {
        this.packet = packet;
    }

    boolean isDecodeHeader() {
        return true;
    }


    public T getBody() {
        return body;
    }
}
