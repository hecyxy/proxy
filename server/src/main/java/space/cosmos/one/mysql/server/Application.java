package space.cosmos.one.mysql.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.mysql.server.endpoint.InstanceServiceGrpcImpl;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        Server server = ServerBuilder.forPort(1888)
                .addService(new InstanceServiceGrpcImpl())
                .build();
        try {
            server.start();
            server.awaitTermination();
            logger.info("grpc server success start at port 1888");
        } catch (Exception e) {
            logger.warn("server start error", e);
        }
    }
}
