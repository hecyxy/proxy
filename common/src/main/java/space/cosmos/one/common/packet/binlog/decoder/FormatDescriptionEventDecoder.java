package space.cosmos.one.common.packet.binlog.decoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.common.packet.binlog.FormatDescriptionEventData;

public class FormatDescriptionEventDecoder implements EventDataDecoder<FormatDescriptionEventData> {
    private static final Logger logger = LoggerFactory.getLogger(FormatDescriptionEventDecoder.class);

    @Override
    public FormatDescriptionEventData decode(byte[] data) {
        FormatDescriptionEventData event = new FormatDescriptionEventData();
        event.read(data);
        logger.info(event.toString());
        return event;
    }
}
