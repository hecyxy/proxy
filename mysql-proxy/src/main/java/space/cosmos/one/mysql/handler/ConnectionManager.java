package space.cosmos.one.mysql.handler;

import io.netty.channel.Channel;
import space.cosmos.one.mysql.threadpool.cached.CachedThreadPool;
import space.cosmos.one.mysql.util.WrapStream;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class ConnectionManager {
    private Map<Long, InternalConnection> connMap = new ConcurrentHashMap<>();
    private static Executor executor = new CachedThreadPool().getExecutor("conn-manager");

    public void replaceOrAdd(ConnectionConfig config) {
        synchronized (this) {
            //getOrPut operation
            long key = config.getId();
            if (!connMap.containsKey(key)) {
                connMap.put(key, new InternalConnection(config));
                ConcurrentHashMap<Channel, WrapStream> instanceMap = new ConcurrentHashMap<>();
                config.setMap(instanceMap);
                config.getList().add(instanceMap);
            }
            InternalConnection conn = connMap.get(config.getId());
            if (!conn.isStart()) {
                executor.execute(() -> connMap.get(config.getId()).start());
            }
        }
    }
}
