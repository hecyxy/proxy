package space.cosmos.one.common.packet.binlog.decoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.common.packet.binlog.ChecksumType;
import space.cosmos.one.common.packet.binlog.EventType;
import space.cosmos.one.common.packet.binlog.TableMapEventData;

import java.util.HashMap;
import java.util.Map;

public class DecoderConfig {
    private static final Logger logger = LoggerFactory.getLogger(DecoderConfig.class);

    public static Map<EventType, EventDataDecoder> decoderMap;

    public static Map<Long, TableMapEventData> tableMapEventByTableId;

    public static ChecksumType checksumType = ChecksumType.NONE;

    public static boolean mayContainExtraInformation = true;

    static {
        decoderMap = new HashMap<>();
        tableMapEventByTableId = new HashMap<>();
        decoderMap.put(EventType.FORMAT_DESCRIPTION, new FormatDescriptionEventDecoder());
        decoderMap.put(EventType.ROTATE, new RotateEventDecoder());
        decoderMap.put(EventType.INTVAR, new IntVarEventDecoder());
        decoderMap.put(EventType.QUERY, new QueryEventDecoder());
        decoderMap.put(EventType.TABLE_MAP, new TableMapEventDecoder());
        decoderMap.put(EventType.XID, new XidEventDecoder());
        decoderMap.put(EventType.WRITE_ROWS, new WriteRowsEventDecoder());
        decoderMap.put(EventType.EXT_WRITE_ROWS, new WriteRowsEventDecoder());
        decoderMap.put(EventType.UPDATE_ROWS, new UpdateRowsEventDecoder());
        decoderMap.put(EventType.EXT_UPDATE_ROWS, new UpdateRowsEventDecoder());
        decoderMap.put(EventType.DELETE_ROWS, new DeleteRowsEventDecoder());
        decoderMap.put(EventType.EXT_DELETE_ROWS, new DeleteRowsEventDecoder());
        decoderMap.put(EventType.ROWS_QUERY, new RowsQueryEventDecoder());
        decoderMap.put(EventType.ANONYMOUS_GTID, new GtidEventDecoder());
        decoderMap.put(EventType.GTID, new GtidEventDecoder());
    }

    public static EventDataDecoder getDecoder(EventType eventType) {
        EventDataDecoder decoder = decoderMap.get(eventType);
        return decoder;
    }

    public static void putTableMap(Long tableId, TableMapEventData tableMapEventData) {
        tableMapEventByTableId.put(tableId, tableMapEventData);
    }

    public static TableMapEventData getTableMap(Long tableId) {
        TableMapEventData tableMapEventData = tableMapEventByTableId.get(tableId);
        if (tableMapEventData == null) {
            logger.error("no match tableMap,tableId=" + tableId);
            throw new RuntimeException("no match tableMap,tableId=" + tableId);
        }
        return tableMapEventData;
    }
}
