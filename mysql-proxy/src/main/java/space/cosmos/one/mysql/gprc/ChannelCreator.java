package space.cosmos.one.mysql.gprc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.channel.ChannelOption;
import space.cosmos.one.mysql.exception.NpeException;

public class ChannelCreator {

    public static ManagedChannel singleChannel(InternalChannelOption option) {
        if (null == option.getAddress()) {
            throw new NpeException("address is empty");
        }
        return ManagedChannelBuilder
                .forAddress(option.getAddress(), option.getPort())
                .usePlaintext().build();
    }

    /**
     * @param option
     * @return
     * @Description 利用dns负载均衡
     */
    public static ManagedChannel dnsChannel(InternalChannelOption option) {
        if (null == option.getAddress()) {
            throw new NpeException("address is empty");
        }
        return NettyChannelBuilder.forTarget("dns:///" + option.getAddress() + ":" + option.getPort())
                .defaultLoadBalancingPolicy("round_robin")
                .maxInboundMetadataSize(option.getMaxInboundMessageSize())
                .withOption(ChannelOption.TCP_NODELAY, true).build();

    }

}
