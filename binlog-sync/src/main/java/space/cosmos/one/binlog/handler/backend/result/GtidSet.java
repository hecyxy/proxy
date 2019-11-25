package space.cosmos.one.binlog.handler.backend.result;

import java.util.*;

public class GtidSet {
    private final Map<String, UUIDSet> map = new LinkedHashMap<>();

    /**
     * @param gtidSet gtid set comprised of closed intervals (like MySQL's executed_gtid_set).
     */
    public GtidSet(String gtidSet) {
        String[] uuidSets = gtidSet.isEmpty() ? new String[0] : gtidSet.split(",");
        for (String uuidSet : uuidSets) {
            int uuidSeparatorIndex = uuidSet.indexOf(":");
            String sourceId = uuidSet.substring(0, uuidSeparatorIndex);
            List<Interval> intervals = new ArrayList<>();
            String[] rawIntervals = uuidSet.substring(uuidSeparatorIndex + 1).split(":");
            for (String interval : rawIntervals) {
                String[] is = interval.split("-");
                long[] split = new long[is.length];
                for (int i = 0, e = is.length; i < e; i++) {
                    split[i] = Long.parseLong(is[i]);
                }
                if (split.length == 1) {
                    split = new long[] {split[0], split[0]};
                }
                intervals.add(new Interval(split[0], split[1]));
            }
            map.put(sourceId, new UUIDSet(sourceId, intervals));
        }
    }

    public Collection<UUIDSet> getUUIDSets() {
        return map.values();
    }

    /**
     * @param gtid GTID ("source_id:transaction_id")
     * @return whether or not gtid was added to the set (false if it was already there)
     */
    public boolean add(String gtid) {
        String[] split = gtid.split(":");
        String sourceId = split[0];
        long transactionId = Long.parseLong(split[1]);
        UUIDSet uuidSet = map.get(sourceId);
        if (uuidSet == null) {
            map.put(sourceId, uuidSet = new UUIDSet(sourceId, new ArrayList<Interval>()));
        }
        List<Interval> intervals = (List<Interval>) uuidSet.intervals;
        int index = findInterval(intervals, transactionId);
        boolean addedToExisting = false;
        if (index < intervals.size()) {
            Interval interval = intervals.get(index);
            if (interval.start == transactionId + 1) {
                interval.start = transactionId;
                addedToExisting = true;
            } else
            if (interval.end + 1 == transactionId) {
                interval.end = transactionId;
                addedToExisting = true;
            } else
            if (interval.start <= transactionId && transactionId <= interval.end) {
                return false;
            }
        }
        if (!addedToExisting) {
            intervals.add(index, new Interval(transactionId, transactionId));
        }
        if (intervals.size() > 1) {
            joinAdjacentIntervals(intervals, index);
        }
        return true;
    }

    /**
     * Collapses intervals like a-(b-1):b-c into a-c (only in index+-1 range).
     */
    private void joinAdjacentIntervals(List<Interval> intervals, int index) {
        for (int i = Math.min(index + 1, intervals.size() - 1), e = Math.max(index - 1, 0); i > e; i--) {
            Interval a = intervals.get(i - 1), b = intervals.get(i);
            if (a.end + 1 == b.start) {
                a.end = b.end;
                intervals.remove(i);
            }
        }
    }

    @Override
    public String toString() {
        List<String> gtids = new ArrayList<>();
        for (UUIDSet uuidSet : map.values()) {
            gtids.add(uuidSet.getUUID() + ":" + join(uuidSet.intervals, ":"));
        }
        return join(gtids, ",");
    }

    /**
     * @return index which is either a pointer to the interval containing v or a position at which v can be added
     */
    private static int findInterval(List<Interval> ii, long v) {
        int l = 0, p = 0, r = ii.size();
        while (l < r) {
            p = (l + r) / 2;
            Interval i = ii.get(p);
            if (i.end < v) {
                l = p + 1;
            } else
            if (v < i.start) {
                r = p;
            } else {
                return p;
            }
        }
        if (!ii.isEmpty() && ii.get(p).end < v) {
            p++;
        }
        return p;
    }

    private String join(Collection o, String delimiter) {
        if (o.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object o1 : o) {
            sb.append(o1).append(delimiter);
        }
        return sb.substring(0, sb.length() - delimiter.length());
    }
   public static final class UUIDSet {
        private String uuid;
        private Collection<Interval> intervals;

        private UUIDSet(String uuid, Collection<Interval> intervals) {
            this.uuid = uuid;
            this.intervals = intervals;
        }

        public String getUUID() {
            return uuid;
        }

        public Collection<Interval> getIntervals() {
            return intervals;
        }
    }

    public static final class Interval implements Comparable<Interval> {

        private long start;
        private long end;

        public Interval(long start, long end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public int compareTo(Interval o) {
            return saturatedCast(this.start - o.start);
        }

        @Override
        public String toString() {
            return start + "-" + end;
        }

        private int saturatedCast(long value) {
            if (value > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
            if (value < Integer.MIN_VALUE) {
                return Integer.MIN_VALUE;
            }
            return (int) value;
        }
    }
}
