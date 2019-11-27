package space.cosmos.one.common.packet.binlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.common.packet.MysqlMessage;
import space.cosmos.one.common.packet.binlog.decoder.DecoderConfig;
import space.cosmos.one.common.packet.binlog.decoder.EventDataDecoder;

public class Event {
    private static final Logger logger = LoggerFactory.getLogger(Event.class);

    private EventHeader eventHeader;
    private EventData eventData;

    public void read(byte[] bin) {
        MysqlMessage mm = new MysqlMessage(bin);
        // skip the marker
        mm.read();
        EventHeader eventHeader = new EventHeader(mm);
        eventHeader.read();
        EventDataDecoder decoder = DecoderConfig.getDecoder(eventHeader.getEventType());
        if (decoder != null) {
            eventData = decoder.decode(mm.readBytes());
        } else {
            logger.warn("don't support this event type,type=" + eventHeader.getEventType());
        }
    }

    public EventHeader getHeader() {
        return eventHeader;
    }

    public void setHeader(EventHeader header) {
        this.eventHeader = header;
    }

    public EventData getData() {
        return eventData;
    }

    public void setData(EventData data) {
        this.eventData = data;
    }

}
