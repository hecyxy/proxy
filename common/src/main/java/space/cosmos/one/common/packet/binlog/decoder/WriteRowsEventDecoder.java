package space.cosmos.one.common.packet.binlog.decoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.common.packet.binlog.WriteRowsEventData;

public class WriteRowsEventDecoder implements EventDataDecoder<WriteRowsEventData> {
    private static final Logger logger = LoggerFactory.getLogger(WriteRowsEventDecoder.class);

    @Override
    public WriteRowsEventData decode(byte[] data) {
        WriteRowsEventData event = new WriteRowsEventData();
        event.read(data);
        logger.info(event.toString());
        return event;
    }
}
