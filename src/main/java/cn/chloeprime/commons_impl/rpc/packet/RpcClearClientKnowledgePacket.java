package cn.chloeprime.commons_impl.rpc.packet;

import cn.chloeprime.commons_impl.rpc.MethodKnowledgeDatabase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record RpcClearClientKnowledgePacket() {
    public void encode(FriendlyByteBuf ignored) {
    }

    public static RpcClearClientKnowledgePacket decode(FriendlyByteBuf ignored) {
        return new RpcClearClientKnowledgePacket();
    }

    public void handle(Supplier<NetworkEvent.Context> ignored) {
        MethodKnowledgeDatabase.clearForClient();
    }
}
