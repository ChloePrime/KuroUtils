package cn.chloeprime.commons_impl.rpc.packet;

import cn.chloeprime.commons_impl.KuroUtilsMod;
import cn.chloeprime.commons_impl.rpc.MethodKnowledgeDatabase;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

public record RpcClearClientKnowledgePacket() implements CustomPacketPayload {
    public static final Type<RpcClearClientKnowledgePacket> TYPE = new Type<>(KuroUtilsMod.loc("rpc_clear_client_knowledge"));
    public static final StreamCodec<ByteBuf, RpcClearClientKnowledgePacket> STREAM_CODEC = StreamCodec.unit(new RpcClearClientKnowledgePacket());

    public void handle(IPayloadContext ignored) {
        MethodKnowledgeDatabase.clearForClient();
    }

    @Override
    public @Nonnull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
