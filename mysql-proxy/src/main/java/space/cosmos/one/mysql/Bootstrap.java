package space.cosmos.one.mysql;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.mysql.codec.HandshakeParser;
import space.cosmos.one.mysql.codec.MysqlParser;
import space.cosmos.one.mysql.gprc.ChannelCreator;
import space.cosmos.one.mysql.gprc.InternalChannelOption;
import space.cosmos.one.mysql.gprc.remoting.CmdRecorderService;
import space.cosmos.one.mysql.gprc.remoting.Recorder;
import space.cosmos.one.mysql.handler.ConnectionConfig;
import space.cosmos.one.mysql.handler.ConnectionManager;
import space.cosmos.one.mysql.threadpool.cached.CachedThreadPool;
import space.cosmos.one.proxy.mysql.InstanceServiceGrpc;
import space.cosmos.one.proxy.mysql.ListInstance;
import space.cosmos.one.proxy.mysql.RecordServiceGrpc;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Bootstrap extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
    private Executor executor = new CachedThreadPool().getExecutor("strap-thread");
    private InstanceServiceGrpc.InstanceServiceStub instanceStub;
    private Recorder recorder;
    private AtomicBoolean connected = new AtomicBoolean(false);
    private CountDownLatch latch = new CountDownLatch(1);
    private StreamObserver<ListInstance.Request> instanceObserver;

    private InternalChannelOption option = new InternalChannelOption();

    private ManagedChannel channel = ChannelCreator.singleChannel(option);
    private ConnectionManager manager;
    private Bootstrap() {
        instanceStub = InstanceServiceGrpc.newStub(channel);
        recorder = new CmdRecorderService(RecordServiceGrpc.newStub(channel));
        manager = new ConnectionManager();
    }

    private StreamObserver<ListInstance.Request> newObserver() {
        return instanceStub.listInstance(new StreamObserver<ListInstance.Response>() {
            @Override
            public void onNext(ListInstance.Response value) {
                connected.set(true);
                value.getInstanceList().forEach(instance -> {
                    InetSocketAddress front = new InetSocketAddress((int) instance.getId() + 1000);
                    InetSocketAddress backend = new InetSocketAddress(instance.getHost(), instance.getPort());
                    ConnectionConfig config = new ConnectionConfig(instance.getId(),front,backend,new MysqlParser());
                    manager.replaceOrAdd(config);
                });
            }

            @Override
            public void onError(Throwable t) {
                connected.set(false);
            }

            @Override
            public void onCompleted() {
                connected.set(false);
            }
        });
    }

    public static void main(String[] args) {
        Bootstrap boot = new Bootstrap();
        try {
            boot.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        executor.execute(() -> recorder.start());
        while (latch.getCount() > 0) {
            if (!connected.get()) {
                instanceObserver = newObserver();
            }
            instanceObserver.onNext(ListInstance.Request.getDefaultInstance());
            try {
                latch.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.warn("latch wait error", e);
            }
        }
    }

    public void shutdown() {
        instanceObserver.onCompleted();
        channel.shutdown();
        latch.countDown();
        recorder.shutdown();
    }
}
