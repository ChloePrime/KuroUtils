package cn.chloeprime.commons_impl.network;

import cn.chloeprime.commons_impl.KuroUtilsMod;
import cn.chloeprime.commons_impl.rpc.packet.RpcMethodAcknowledgmentPacket;
import cn.chloeprime.commons_impl.rpc.packet.RpcCallMethodPacket;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class KUNetwork {
    public static final String VERSION = "1.0.0";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            KuroUtilsMod.loc("play_channel"),
            () -> VERSION, VERSION::equals, VERSION::equals
    );

    public static void init() {
        int id = -1;
        CHANNEL.registerMessage(++id, RpcCallMethodPacket.class, RpcCallMethodPacket::encode, RpcCallMethodPacket::decode, RpcCallMethodPacket::handle);
        CHANNEL.registerMessage(++id, RpcMethodAcknowledgmentPacket.class, RpcMethodAcknowledgmentPacket::encode, RpcMethodAcknowledgmentPacket::decode, RpcMethodAcknowledgmentPacket::handle);
    }

    private KUNetwork() {
    }
}
