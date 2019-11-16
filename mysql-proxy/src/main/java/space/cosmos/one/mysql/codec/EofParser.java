package space.cosmos.one.mysql.codec;

import io.netty.buffer.ByteBuf;
import space.cosmos.one.mysql.codec.message.EofMessage;
import space.cosmos.one.mysql.util.ServerStatus;

import java.util.EnumSet;

public class EofParser extends MysqlParser<EofMessage> {
    private EofMessage.Type type;

    public EofParser(ByteBuf in, EofMessage.Type type) {
        super(in);
        this.type = type;
    }

    @Override
    public EofMessage decodeBody0() {
        packet.skipBytes(1);//0xFE
        int warnings = packet.readUnsignedShortLE(); //2字节
        int statusFlags = packet.readUnsignedShortLE();//状态码
        EnumSet<ServerStatus> statuses = toEnumSet(statusFlags);
        return new EofMessage(this.packetLen, this.packetNo, warnings, statuses, type);
    }

    private EnumSet<ServerStatus> toEnumSet(long vector) {
        EnumSet<ServerStatus> set = EnumSet.noneOf(ServerStatus.class);
        for (ServerStatus e : ServerStatus.values()) {
            if ((e.code() & vector) > 0)
                set.add(e);
        }
        return set;
    }
}
