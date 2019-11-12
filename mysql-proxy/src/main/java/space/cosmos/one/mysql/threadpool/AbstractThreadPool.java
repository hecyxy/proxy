package space.cosmos.one.mysql.threadpool;

import space.cosmos.one.mysql.threadpool.config.ThreadPoolConfig;
import space.cosmos.one.mysql.threadpool.config.ThreadPoolConfigFactory;
import sun.nio.ch.ThreadPool;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public abstract class AbstractThreadPool implements InternalThreadPool {


    @Override
    public Executor getExecutor(String name) {
        return getExecutor(ThreadPoolConfigFactory.defaultConfig(name));
    }

    public abstract Executor getExecutor(ThreadPoolConfig config);

}
