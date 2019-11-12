package space.cosmos.one.mysql.threadpool;

import space.cosmos.one.mysql.threadpool.config.ThreadPoolConfig;

import java.util.concurrent.Executor;

public interface InternalThreadPool {

    Executor getExecutor(String name);

    Executor getExecutor(ThreadPoolConfig config);
}
