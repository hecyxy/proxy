package space.cosmos.one.mysql.rpc;

import com.google.protobuf.util.JsonFormat;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.testng.annotations.Test;
import space.cosmos.one.mysql.TestGrpcBase;
import space.cosmos.one.proxy.mysql.CommunicateServiceGrpc;
import space.cosmos.one.proxy.mysql.Info;

import java.util.stream.Stream;

public class TestGrpcClient extends TestGrpcBase {


    CommunicateServiceGrpc.CommunicateServiceBlockingStub blockingStub = CommunicateServiceGrpc.newBlockingStub(channel);

    CommunicateServiceGrpc.CommunicateServiceFutureStub futureStub = CommunicateServiceGrpc.newFutureStub(channel);
    CommunicateServiceGrpc.CommunicateServiceStub newStub = CommunicateServiceGrpc.newStub(channel);

    public static void main(String[] args) {
        TestGrpcClient client = new TestGrpcClient();

    }


    void single() {

    }

    void c2s() {
        StreamObserver<Info> request = newStub.c2s(new StreamObserver<Info>() {
            @Override
            public void onNext(Info value) {
                System.out.println(value.getMsg() + ":" + value.getFlag());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {

            }
        });
        Stream.of("1", "2", "3").map(e -> Info.newBuilder().setMsg(String.valueOf(e)).setFlag("stream client").build())
                .forEach(request::onNext);
        try {
            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void s2c() {
        for (int i = 0; i < 3; i++) {
            newStub.s2c(Info.newBuilder().setFlag("stream server").setMsg(String.valueOf(i)).build(), new StreamObserver<Info>() {
                @Override
                public void onNext(Info value) {
                    System.out.println(value.getMsg() + " : " + value.getFlag());
                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onCompleted() {
                    System.out.println("end...");
                }
            });
        }
    }

    @Test
    void s2s() {
         newStub.double_(new StreamObserver<Info>() {
             @Override
             public void onNext(Info value) {
                 System.out.println(value.getMsg() + " s2s " + value.getFlag());
             }

             @Override
             public void onError(Throwable t) {

             }

             @Override
             public void onCompleted() {
                 System.out.println("s2s to end...");
             }
         });

    }
}
