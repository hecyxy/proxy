package space.cosmos.one.mysql.codec;

import io.netty.buffer.ByteBuf;
import space.cosmos.one.mysql.constant.RemotingType;

public interface Parser {

    void parse(ByteBuf buffer, RemotingType type);

    void close();

    void setUserAddress(String address);
}
