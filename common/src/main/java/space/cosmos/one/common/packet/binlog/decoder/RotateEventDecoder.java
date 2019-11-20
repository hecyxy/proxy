package space.cosmos.one.common.packet.binlog.decoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.common.packet.binlog.RotateEventData;

public class RotateEventDecoder implements EventDataDecoder<RotateEventData> {
    private static final Logger logger = LoggerFactory.getLogger(RotateEventDecoder.class);

    @Override
    public RotateEventData decode(byte[] data) {
        RotateEventData event = new RotateEventData();
        event.read(data);
        logger.info(event.toString());
        return event;
    }
}
