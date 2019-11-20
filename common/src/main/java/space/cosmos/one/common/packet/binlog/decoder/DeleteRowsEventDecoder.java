package space.cosmos.one.common.packet.binlog.decoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.common.packet.binlog.DeleteRowsEventData;

public class DeleteRowsEventDecoder implements EventDataDecoder<DeleteRowsEventData> {

    private static final Logger logger = LoggerFactory.getLogger(DeleteRowsEventDecoder.class);

    @Override
    public DeleteRowsEventData decode(byte[] data) {
        DeleteRowsEventData event = new DeleteRowsEventData();
        event.read(data);
        logger.info(event.toString());
        return event;
    }
}
