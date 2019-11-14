package space.cosmos.one.mysql;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import org.jctools.queues.MpscChunkedArrayQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.mysql.codec.MysqlParser;
import space.cosmos.one.mysql.gprc.ChannelCreator;
import space.cosmos.one.mysql.gprc.InternalChannelOption;
import space.cosmos.one.mysql.gprc.remoting.CmdRecorderService;
import space.cosmos.one.mysql.gprc.remoting.Recorder;
import space.cosmos.one.mysql.handler.ConnectionConfig;
import space.cosmos.one.mysql.handler.ConnectionManager;
import space.cosmos.one.mysql.threadpool.cached.CachedThreadPool;
import space.cosmos.one.mysql.util.CmdInfo;
import space.cosmos.one.proxy.mysql.InstanceServiceGrpc;
import space.cosmos.one.proxy.mysql.ListInstance;
import space.cosmos.one.proxy.mysql.RecordServiceGrpc;

import java.net.InetSocketAddress;
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
    private MpscChunkedArrayQueue<CmdInfo> mpsc;
    private ScheduledExecutorService scheduler;

    private Bootstrap() {
        instanceStub = InstanceServiceGrpc.newStub(channel);
        recorder = new CmdRecorderService(RecordServiceGrpc.newStub(channel));
        manager = new ConnectionManager();
        this.mpsc = new MpscChunkedArrayQueue<>(2048);
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
                    ConnectionConfig config = new ConnectionConfig(instance.getId(), front, backend, mpsc);
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
        Runtime.getRuntime().addShutdownHook(new Thread(() -> boot.shutdown()));
    }

    @Override
    public void run() {
        executor.execute(() -> recorder.start());
        scheduler.scheduleAtFixedRate(this::poll, 0, 1000, TimeUnit.MILLISECONDS);
        while (latch.getCount() > 0) {
            if (!connected.get()) {
                instanceObserver = newObserver();
            }
            instanceObserver.onNext(ListInstance.Request.getDefaultInstance());
            try {
                latch.await(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.warn("latch wait error", e);
            }
        }
    }

    private void poll() {
        CmdInfo cmdInfo = mpsc.poll();
        System.out.println(cmdInfo == null);
        while (cmdInfo != null) {
            cmdInfo.parse();
//            if(cmdInfo.getRequest()!=null && ReferenceCountUtil.refCnt(cmdInfo.getRequest())>0){
//                ReferenceCountUtil.release(cmdInfo.getRequest());
//            }
//
//            if(cmdInfo.getResponse()!=null && ReferenceCountUtil.refCnt(cmdInfo.getResponse())>0){
//                ReferenceCountUtil.release(cmdInfo.getResponse());
//            }

            cmdInfo = mpsc.poll();
        }

    }

    public void shutdown() {
        instanceObserver.onCompleted();
        channel.shutdown();
        latch.countDown();
        recorder.shutdown();
        scheduler.shutdown();
    }
}
