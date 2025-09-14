package cn.chloeprime.commons_impl.rpc.packet;

import cn.chloeprime.commons.ContextUtil;
import cn.chloeprime.commons.rpc.exception.RpcException;
import cn.chloeprime.commons_impl.rpc.*;
import cn.chloeprime.commons_impl.rpc.serialization.RpcSerializationManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

public record RpcCallMethodPacket(
        MethodID methodID,
        Endpoint sender,
        LambdaReflectResult method,
        Object[] arguments,
        boolean isValid
) {
    public RpcCallMethodPacket(MethodID methodID, Endpoint sender, LambdaReflectResult lambda, Object[] arguments) {
        this(methodID, sender, lambda, arguments, true);
    }

    public static RpcCallMethodPacket INVALID = new RpcCallMethodPacket(MethodID.of(0), null, null, new Object[0], false);

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(methodID.value());
        buf.writeUUID(sender.id());
        RpcSerializationManager.write(buf, method, arguments);
    }

    public static RpcCallMethodPacket decode(FriendlyByteBuf buf) {
        var id = MethodID.of(buf.readVarInt());
        var sender = new Endpoint(buf.readUUID());
        var known = MethodKnowledgeDatabase.getLocalKnowledge(sender, id);
        if (known == null) {
            RpcSupport.LOGGER.error("Unknown method id {}", id.value(), new RpcException("Unknown method id"));
            buf.readerIndex(buf.writerIndex());
            return INVALID;
        }
        if (!RpcSupport.validateRpcMethod(known.method()) || !RpcSupport.validateRpcCall(known, ContextUtil.getLocalEndpoint())) {
            buf.readerIndex(buf.writerIndex());
            return INVALID;
        }

        var args = RpcSerializationManager.read(buf, known);
        return new RpcCallMethodPacket(id, sender, known, args);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        if (isValid()) {
            context.enqueueWork(this::call);
        }
        context.setPacketHandled(true);
    }

    public static final ThreadLocal<Deque<Endpoint>> CONTEXT = ThreadLocal.withInitial(ArrayDeque::new);
    private void call() {
        var contextStack = CONTEXT.get();
        try {
            contextStack.push(this.sender());
            method.handle().invokeWithArguments(arguments);
        } catch (Throwable ex) {
            RpcSupport.LOGGER.error("Failed to invoke RPC method", ex);
        } finally {
            contextStack.pop();
        }
    }
}
