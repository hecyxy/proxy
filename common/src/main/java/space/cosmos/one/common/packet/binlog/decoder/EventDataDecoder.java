package space.cosmos.one.common.packet.binlog.decoder;

import space.cosmos.one.common.packet.binlog.EventData;

public interface EventDataDecoder<T extends EventData> {
    T decode(byte[] data);
}
