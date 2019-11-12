package space.cosmos.one.mysql.codec;

import io.netty.buffer.ByteBuf;

public abstract class Parser<T> {

    protected ByteBuf packet;
    private T body;
}
