package space.cosmos.one.common.packet.binlog.decoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.common.packet.binlog.UpdateRowsEventData;

public class UpdateRowsEventDecoder implements EventDataDecoder<UpdateRowsEventData> {
    private static final Logger logger = LoggerFactory.getLogger(UpdateRowsEventDecoder.class);

    @Override
    public UpdateRowsEventData decode(byte[] data) {
        UpdateRowsEventData event = new UpdateRowsEventData();
        event.read(data);
        logger.info(event.toString());
        return null;
    }
}
