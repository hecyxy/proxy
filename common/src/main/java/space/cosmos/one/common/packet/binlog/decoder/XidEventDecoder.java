package space.cosmos.one.common.packet.binlog.decoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.common.packet.binlog.XidEventData;

public class XidEventDecoder implements EventDataDecoder<XidEventData> {
    private static final Logger logger = LoggerFactory.getLogger(XidEventDecoder.class);

    @Override
    public XidEventData decode(byte[] data) {
        XidEventData event = new XidEventData();
        event.read(data);
        logger.info(event.toString());
        return event;
    }
}
