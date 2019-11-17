package space.cosmos.one.mysql;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.Test;
import space.cosmos.one.proxy.mysql.InstanceServiceGrpc;
import space.cosmos.one.proxy.mysql.ListInstance;

import java.util.stream.Stream;

public class TestClient {
    private static void println(String msg) {
        System.out.println(msg);
    }

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
        InstanceServiceGrpc.InstanceServiceStub stub = InstanceServiceGrpc.newStub(channel);
        ListInstance.Request.Builder request = ListInstance.Request.newBuilder();
        StreamObserver<ListInstance.Request> streamRequest = stub.listInstance(new StreamObserver<ListInstance.Response>() {

            @Override
            public void onNext(ListInstance.Response value) {
                try {
                    println(JsonFormat.printer().print(value));
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {

            }
        });
        Stream.of("1", "2", "3")
                .map(m -> ListInstance.Request.newBuilder().setVersion(m).build()
                ).forEach(f -> {
            streamRequest.onNext(f);
        });
        streamRequest.onCompleted();
        Thread.sleep(3 * 1000);
    }

    @Test
    public void test1() {
        int a = 10;
        while (true) {
            switch (a) {
                case 1:
                    System.out.println("aa");
                    break;
                case 10:
                    System.out.println("aa");
                    break;
                default:
                    System.out.println("aa");
                    break;
            }
            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
