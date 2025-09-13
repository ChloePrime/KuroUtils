package cn.chloeprime.commons_impl.rpc.packet;

import cn.chloeprime.commons_impl.KuroUtilsMod;
import cn.chloeprime.commons_impl.rpc.LambdaReflectResult;
import cn.chloeprime.commons_impl.rpc.MethodKnowledgeDatabase;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record RpcMethodAcknowledgmentPacket(
        int id,
        String clazz,
        String methodName,
        String methodSignature
) implements CustomPacketPayload {
    public static final Type<RpcMethodAcknowledgmentPacket> TYPE = new Type<>(KuroUtilsMod.loc("rpc_knowledge"));

    public static final StreamCodec<ByteBuf, RpcMethodAcknowledgmentPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, RpcMethodAcknowledgmentPacket::id,
            ByteBufCodecs.STRING_UTF8, RpcMethodAcknowledgmentPacket::clazz,
            ByteBufCodecs.STRING_UTF8, RpcMethodAcknowledgmentPacket::methodName,
            ByteBufCodecs.STRING_UTF8, RpcMethodAcknowledgmentPacket::methodSignature,
            RpcMethodAcknowledgmentPacket::new
    );

    public RpcMethodAcknowledgmentPacket(int id, LambdaReflectResult lambda) {
        this(id, lambda.className(), lambda.methodName(), lambda.methodSignature());
    }

    public void handle(IPayloadContext context) {
        MethodKnowledgeDatabase.putLocalKnowledge(context, this);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
