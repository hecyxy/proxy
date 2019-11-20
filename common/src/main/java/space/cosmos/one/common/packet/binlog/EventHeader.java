package space.cosmos.one.common.packet.binlog;

import space.cosmos.one.common.packet.MysqlMessage;

public class EventHeader {
    private static final EventType[] EVENT_TYPES = EventType.values();

    private MysqlMessage mysqlMessage;

    private long timeStamp;
    private EventType eventType;
    private long serverId;
    private long eventLength;

    private long nextPosition;
    private int flags;

    public EventHeader(MysqlMessage message) {
        this.mysqlMessage = message;
    }


    public void read() {
        timeStamp = mysqlMessage.readUnsignedLongLE() * 1000;
        eventType = getEventType(mysqlMessage.read());
        serverId = mysqlMessage.readUnsignedLongLE();
        eventLength = mysqlMessage.readUnsignedLongLE();
        nextPosition = mysqlMessage.readUnsignedLongLE();
        flags = mysqlMessage.readUnsignedIntLE();

    }

    public static EventType getEventType(int ordinal) {
        if (ordinal > EVENT_TYPES.length) {
            throw new RuntimeException("unknown event type" + ordinal);
        }
        return EVENT_TYPES[ordinal];
    }

    public static EventType[] getEventTypes() {
        return EVENT_TYPES;
    }

    public MysqlMessage getMysqlMessage() {
        return mysqlMessage;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public EventType getEventType() {
        return eventType;
    }

    public long getServerId() {
        return serverId;
    }

    public long getEventLength() {
        return eventLength;
    }

    public long getNextPosition() {
        return nextPosition;
    }

    public int getFlags() {
        return flags;
    }

    public long getDataLength() {
        return getEventLength() - getHeaderLength();
    }
    public long getHeaderLength() {
        return 19;
    }
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("EventHeaderV4");
        sb.append("{timestamp=").append(timeStamp);
        sb.append(", eventType=").append(eventType);
        sb.append(", serverId=").append(serverId);
        sb.append(", headerLength=").append(getHeaderLength());
        sb.append(", dataLength=").append(getDataLength());
        sb.append(", nextPosition=").append(nextPosition);
        sb.append(", flags=").append(flags);
        sb.append('}');
        return sb.toString();
    }
}
