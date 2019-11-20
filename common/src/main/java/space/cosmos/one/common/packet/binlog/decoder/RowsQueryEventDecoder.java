package space.cosmos.one.common.packet.binlog.decoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.common.packet.binlog.RowsQueryEventData;

public class RowsQueryEventDecoder implements EventDataDecoder<RowsQueryEventData> {
    private static final Logger logger = LoggerFactory.getLogger(RowsQueryEventDecoder.class);

    @Override
    public RowsQueryEventData decode(byte[] data) {
        RowsQueryEventData event = new RowsQueryEventData();
        event.read(data);
        logger.info(event.toString());
        return event;
    }
}
