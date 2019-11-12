package space.cosmos.one.mysql.gprc.remoting;

import space.cosmos.one.proxy.mysql.RecordItem;

public interface Recorder {
    public void add(RecordItem item);

    public void start();

    public void shutdown();
}
