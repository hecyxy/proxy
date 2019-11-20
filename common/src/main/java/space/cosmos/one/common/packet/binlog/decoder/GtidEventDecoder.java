package space.cosmos.one.common.packet.binlog.decoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.common.packet.binlog.GtidEventData;

public class GtidEventDecoder implements EventDataDecoder<GtidEventData> {
    private static final Logger logger = LoggerFactory.getLogger(GtidEventDecoder.class);

    @Override
    public GtidEventData decode(byte[] data) {
        GtidEventData event = new GtidEventData();
        event.read(data);
        logger.info(event.toString());
        return event;
    }
}
