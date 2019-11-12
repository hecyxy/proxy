package space.cosmos.one.mysql.threadpool.cached;

import space.cosmos.one.mysql.threadpool.AbortPolicyWithReport;
import space.cosmos.one.mysql.threadpool.AbstractThreadPool;
import space.cosmos.one.mysql.threadpool.NamedThreadFactory;
import space.cosmos.one.mysql.threadpool.config.ThreadPoolConfig;

import java.util.concurrent.*;

public class CachedThreadPool extends AbstractThreadPool {

    @Override
    public Executor getExecutor(ThreadPoolConfig config) {
        return getExecutor(config.getPrefixName(), config.getCoreSize(), config.getMaxSize(), config.getQueues(),
                config.getAlive());
    }

    public Executor getExecutor(String name, int coreSize, int maxSize, int queues, int alive) {
        return new ThreadPoolExecutor(coreSize, maxSize, alive, TimeUnit.MILLISECONDS,
                queues == 0 ? new SynchronousQueue<>() :
                        (queues < 0 ? new LinkedBlockingQueue<>()
                                : new LinkedBlockingQueue<>(queues)),
                new NamedThreadFactory(name, true), new AbortPolicyWithReport(name));
    }
}
