package cn.chloeprime.commons_impl.rpc.packet;

import cn.chloeprime.commons_impl.rpc.LambdaReflectResult;
import cn.chloeprime.commons_impl.rpc.MethodKnowledgeDatabase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record RpcMethodAcknowledgmentPacket(
        int id,
        String clazz,
        String methodName,
        String methodSignature
) {
    public RpcMethodAcknowledgmentPacket(int id, LambdaReflectResult lambda) {
        this(id, lambda.className(), lambda.methodName(), lambda.methodSignature());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(id);
        buffer.writeUtf(clazz);
        buffer.writeUtf(methodName);
        buffer.writeUtf(methodSignature);
    }

    public static RpcMethodAcknowledgmentPacket decode(FriendlyByteBuf buffer) {
        var id = buffer.readVarInt();
        var clazz = buffer.readUtf();
        var methodName = buffer.readUtf();
        var methodSign = buffer.readUtf();
        return new RpcMethodAcknowledgmentPacket(id, clazz, methodName, methodSign);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        MethodKnowledgeDatabase.putLocalKnowledge(context, this);
        context.setPacketHandled(true);
    }
}
