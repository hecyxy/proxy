package space.cosmos.one.mysql.server.endpoint;

import io.grpc.stub.StreamObserver;
import space.cosmos.one.mysql.server.config.DbConfig;
import space.cosmos.one.proxy.mysql.InstanceServiceGrpc;
import space.cosmos.one.proxy.mysql.ListInstance;

import java.util.Arrays;
import java.util.List;

public class InstanceServiceGrpcImpl extends InstanceServiceGrpc.InstanceServiceImplBase {

    private static String dbUrl1;
    private static String dbUrl2;

    static {
        dbUrl1 = DbConfig.getConfig().getString("mysql.server1");
        dbUrl2 = DbConfig.getConfig().getString("mysql.server1");
    }

    @Override
    public StreamObserver<ListInstance.Request> listInstance(StreamObserver<ListInstance.Response> responseObserver) {

        return new StreamObserver<ListInstance.Request>() {
            List<ListInstance.Response.Instance> list = Arrays.asList(
                    //import yours dburl and port
                    ListInstance.Response.Instance.newBuilder().setHost(dbUrl1).setId(1).setPort(3306).build(),
                    ListInstance.Response.Instance.newBuilder().setHost(dbUrl2).setId(2).setPort(3306).build());


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
