package space.cosmos.one.mysql;


import com.google.protobuf.Empty;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import space.cosmos.one.proxy.mysql.InstanceServiceGrpc;
import space.cosmos.one.proxy.mysql.ListInstance;
import space.cosmos.one.proxy.mysql.RecordItem;
import space.cosmos.one.proxy.mysql.RecordServiceGrpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

class RecordServiceGrpcImp extends RecordServiceGrpc.RecordServiceImplBase {

    @Override
    public StreamObserver<RecordItem> record(StreamObserver<Empty> responseObserver) {

        return new StreamObserver<RecordItem>() {
            @Override
            public void onNext(RecordItem value) {
                responseObserver.onNext(Empty.getDefaultInstance());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}

class InstanceServiceGrpcImpl extends InstanceServiceGrpc.InstanceServiceImplBase {
    @Override
    public StreamObserver<ListInstance.Request> listInstance(StreamObserver<ListInstance.Response> responseObserver) {
        return new StreamObserver<ListInstance.Request>() {
            ArrayList<ListInstance.Response.Instance> itemList = new ArrayList<>(10);
            AtomicInteger count = new AtomicInteger(1);

            @Override
            public void onNext(ListInstance.Request value) {
                System.out.println(value.getVersion());
                ListInstance.Response.Instance.Builder builder = ListInstance.Response.Instance.newBuilder();
                builder.setId(count.getAndIncrement());
                builder.setHost("192.168.8.13");
                builder.setPort(3306);
                itemList.add(builder.build());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                ListInstance.Response.Builder resp = ListInstance.Response.newBuilder();
                resp.addAllInstance(itemList);
                responseObserver.onNext(resp.build());
                responseObserver.onCompleted();
            }
        };
    }
}

public class TestServer {
    public static void main(String[] args) {
        Server server = ServerBuilder.forPort(9090)
                .addService(new RecordServiceGrpcImp())
                .addService(new InstanceServiceGrpcImpl())
                .build();

        try {
            server.start();
            server.awaitTermination();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
