package space.cosmos.one.common.packet.binlog.decoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.common.packet.binlog.QueryEventData;

public class QueryEventDecoder implements EventDataDecoder<QueryEventData> {
    private static final Logger logger = LoggerFactory.getLogger(QueryEventDecoder.class);

    @Override
    public QueryEventData decode(byte[] data) {
        QueryEventData event = new QueryEventData();
        event.read(data);
        logger.info(event.toString());
        return event;
    }
}
