package space.cosmos.one.common.packet.binlog.decoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.common.packet.binlog.IntVarEventData;

public class IntVarEventDecoder implements EventDataDecoder<IntVarEventData> {
    private static final Logger logger = LoggerFactory.getLogger(IntVarEventDecoder.class);

    @Override
    public IntVarEventData decode(byte[] data) {
        IntVarEventData event = new IntVarEventData();
        event.read(data);
        logger.info(event.toString());
        return event;
    }
}
