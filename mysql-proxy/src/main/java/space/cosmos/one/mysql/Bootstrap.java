package space.cosmos.one.mysql;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.mysql.gprc.ChannelCreator;
import space.cosmos.one.mysql.gprc.InternalChannelOption;
import space.cosmos.one.mysql.gprc.remoting.CmdRecorderService;
import space.cosmos.one.mysql.gprc.remoting.Recorder;
import space.cosmos.one.mysql.handler.ConnectionConfig;
import space.cosmos.one.mysql.handler.ConnectionManager;
import space.cosmos.one.mysql.threadpool.cached.CachedThreadPool;
import space.cosmos.one.mysql.util.WrapStream;
import space.cosmos.one.proxy.mysql.InstanceServiceGrpc;
import space.cosmos.one.proxy.mysql.ListInstance;
import space.cosmos.one.proxy.mysql.RecordServiceGrpc;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.*;
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
    private ArrayList<ConcurrentHashMap<Channel, WrapStream>> instanceList;
    private ScheduledExecutorService scheduler;

    private Executor timePool = new CachedThreadPool().getExecutor("timer-thread");

    private Bootstrap() {
        instanceStub = InstanceServiceGrpc.newStub(channel);
        recorder = new CmdRecorderService(RecordServiceGrpc.newStub(channel));
        manager = new ConnectionManager();
        this.instanceList = new ArrayList<>(2048);
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    private StreamObserver<ListInstance.Request> newObserver() {
        return instanceStub.listInstance(new StreamObserver<ListInstance.Response>() {
            @Override
            public void onNext(ListInstance.Response value) {
                connected.set(true);
                value.getInstanceList().forEach(instance -> {
                    logger.info(String.format("[instance msg] id: %s; host:%s port:%s", instance.getId(), instance.getHost(), instance.getPort()));
                    InetSocketAddress front = new InetSocketAddress((int) instance.getId() + 1024);
                    InetSocketAddress backend = new InetSocketAddress(instance.getHost(), instance.getPort());
                    ConnectionConfig config = new ConnectionConfig(instance.getId(), front, backend, instanceList);
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
        Runtime.getRuntime().addShutdownHook(new Thread(boot::shutdown));
    }

    @Override
    public void run() {
        executor.execute(() -> recorder.start());
        scheduler.scheduleAtFixedRate(this::poll, 0, 5, TimeUnit.SECONDS);
        while (latch.getCount() > 0) {
            if (!connected.get()) {
                instanceObserver = newObserver();
            }
            instanceObserver.onNext(ListInstance.Request.getDefaultInstance());
            instanceObserver.onCompleted();
            try {
                latch.await(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.warn("latch wait error", e);
            }
        }
    }

    private void poll() {
        logger.info("list size {}", instanceList.size());
        instanceList.forEach(map -> {
            logger.info("map size {}", map.size());
            map.forEachEntry(2, entry -> {
                logger.info("running...");
                WrapStream cmdInfo = entry.getValue();
                if (!cmdInfo.getStarted()) {
                    timePool.execute(cmdInfo);
                    cmdInfo.setStarted(true);
                    logger.info(String.format("remote addr %s,start: %s", entry.getValue().getRemoteAddress(), cmdInfo.getStarted()));
                }
            });
        });


    }

    private void shutdown() {
        try {
            instanceObserver.onCompleted();
        } catch (Exception e) {
            logger.warn("grpc close exception,can be ignored...");
        } finally {
            channel.shutdown();
            latch.countDown();
            recorder.shutdown();
            scheduler.shutdown();
            instanceList.clear();
        }

    }
}
