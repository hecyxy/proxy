package space.cosmos.one.mysql.gprc.remoting;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.proxy.mysql.RecordItem;
import space.cosmos.one.proxy.mysql.RecordServiceGrpc;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CmdRecorderService implements Recorder {
    private static final Logger logger = LoggerFactory.getLogger(CmdRecorderService.class);
    private RecordServiceGrpc.RecordServiceStub stub;
    private CountDownLatch latch = new CountDownLatch(1);
    private ArrayBlockingQueue<RecordItem> queue = new ArrayBlockingQueue<>(8888);
    private StreamObserver<RecordItem> observer;
    private AtomicBoolean connected = new AtomicBoolean(false);

    public CmdRecorderService(RecordServiceGrpc.RecordServiceStub stub) {
        this.stub = stub;
    }

    private StreamObserver<RecordItem> newObserver() {
        return stub.record(new StreamObserver<Empty>() {
            @Override
            public void onNext(Empty value) {
                connected.set(true);
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

    @Override
    public void start() {
        while (latch.getCount() > 0) {
            if (!connected.get()) {
                observer = newObserver();
            }
            RecordItem item = null;
            try {
                item = queue.poll(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.warn("queue poll exception", e);
            }
            observer.onNext(item);
        }
    }

    @Override
    public void add(RecordItem item) {
        try {
            queue.put(item);
        } catch (InterruptedException e) {
            logger.warn("queue add exception", e);
        }
    }

    @Override
    public void shutdown() {
        latch.countDown();
        observer.onCompleted();
    }
}
