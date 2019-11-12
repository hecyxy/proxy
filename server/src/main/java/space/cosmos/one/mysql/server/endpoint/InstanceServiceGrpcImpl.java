package space.cosmos.one.mysql.server.endpoint;

import io.grpc.stub.StreamObserver;
import space.cosmos.one.proxy.mysql.InstanceServiceGrpc;
import space.cosmos.one.proxy.mysql.ListInstance;

import java.util.Arrays;
import java.util.List;

public class InstanceServiceGrpcImpl extends InstanceServiceGrpc.InstanceServiceImplBase {

    @Override
    public StreamObserver<ListInstance.Request> listInstance(StreamObserver<ListInstance.Response> responseObserver) {

        return new StreamObserver<ListInstance.Request>() {
            List<ListInstance.Response.Instance> list = Arrays.asList(
                    ListInstance.Response.Instance.newBuilder().setHost("172.16.52.81").setId(1).setPort(3306).build());
//                    ListInstance.Response.Instance.newBuilder().setHost("192.168.10.12").setId(2).setPort(1026).build(),
//                    ListInstance.Response.Instance.newBuilder().setHost("192.168.10.12").setId(3).setPort(1027).build());


            @Override
            public void onNext(ListInstance.Request value) {
                responseObserver.onNext(ListInstance.Response.newBuilder().addAllInstance(list).build());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {

            }
        };
    }
}
