package space.cosmos.one.mysql;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

public abstract class TestGrpcBase {


    public static ManagedChannel channel;

    @BeforeSuite
    @BeforeClass
    public void init() {
        channel = ManagedChannelBuilder.forAddress("localhost", 9899)
                .build();
        System.out.println("init success ");
    }
}
