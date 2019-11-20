package space.cosmos.one.common.packet.binlog.decoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.common.packet.binlog.TableMapEventData;

public class TableMapEventDecoder implements EventDataDecoder<TableMapEventData> {
    private static final Logger logger = LoggerFactory.getLogger(TableMapEventDecoder.class);

    @Override
    public TableMapEventData decode(byte[] data) {
        TableMapEventData eventData = new TableMapEventData();
        eventData.read(data);
        logger.info(eventData.toString());
        return eventData;
    }
}
