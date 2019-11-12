package space.cosmos.one.mysql.rpc;


import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import space.cosmos.one.proxy.mysql.CommunicateServiceGrpc;
import space.cosmos.one.proxy.mysql.Info;

import java.util.ArrayList;
import java.util.List;

class CommunicateServiceGrpcImpl extends CommunicateServiceGrpc.CommunicateServiceImplBase {
    @Override
    public void single(Info request, StreamObserver<Info> responseObserver) {
        responseObserver.onNext(Info.newBuilder().setMsg("receive").setFlag("sing").build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Info> c2s(StreamObserver<Info> responseObserver) {
        return new StreamObserver<Info>() {
            @Override
            public void onNext(Info value) {
                System.err.println("stream client receive " + value.getMsg());
                responseObserver.onNext(Info.newBuilder().setMsg("stream client end").setFlag("sc").build());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(Info.newBuilder().setMsg("stream client end").setFlag("sc").build());
            }
        };
    }

    @Override
    public void s2c(Info request, StreamObserver<Info> responseObserver) {
        for (int i = 0; i < 5; i++) {
            responseObserver.onNext(Info.newBuilder().setMsg(String.valueOf(i)).setFlag("s 2 c").build());
        }
    }

    @Override
    public StreamObserver<Info> double_(StreamObserver<Info> responseObserver) {
        return new StreamObserver<Info>() {
            List<String> str = new ArrayList<>(5);

            @Override
            public void onNext(Info value) {
                str.add(value.getMsg());
                responseObserver.onNext(Info.newBuilder().setMsg("server on next").build());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                for (int i = 0; i < 4; i++) {
                    responseObserver.onNext(Info.newBuilder().setMsg(String.valueOf(i)).build());
                }
                responseObserver.onCompleted();
            }
        };
    }
}

public class TestGrpcServer {

    public static void main(String[] args) {
        Server server = ServerBuilder.forPort(9899)
                .addService(new CommunicateServiceGrpcImpl())
                .build();
        try {
            server.start();
            server.awaitTermination();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
