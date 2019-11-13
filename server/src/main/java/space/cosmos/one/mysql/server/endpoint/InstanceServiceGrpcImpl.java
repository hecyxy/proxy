package space.cosmos.one.mysql.server.endpoint;

import io.grpc.stub.StreamObserver;
import space.cosmos.one.proxy.mysql.InstanceServiceGrpc;
import space.cosmos.one.proxy.mysql.ListInstance;

import java.util.Arrays;
import java.util.List;

import static space.cosmos.one.mysql.server.config.DbConfig.dbUrl;
import static space.cosmos.one.mysql.server.config.DbConfig.dbUrl2;

public class InstanceServiceGrpcImpl extends InstanceServiceGrpc.InstanceServiceImplBase {

    @Override
    public StreamObserver<ListInstance.Request> listInstance(StreamObserver<ListInstance.Response> responseObserver) {

        return new StreamObserver<ListInstance.Request>() {
            List<ListInstance.Response.Instance> list = Arrays.asList(
                    //import yours dburl and port
                    ListInstance.Response.Instance.newBuilder().setHost(dbUrl).setId(1).setPort(3306).build(),
                    ListInstance.Response.Instance.newBuilder().setHost(dbUrl2).setId(2).setPort(3306).build());
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
