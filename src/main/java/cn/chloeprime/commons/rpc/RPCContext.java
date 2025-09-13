package cn.chloeprime.commons.rpc;

import cn.chloeprime.commons.rpc.exception.RpcException;
import cn.chloeprime.commons_impl.rpc.Endpoint;
import cn.chloeprime.commons_impl.rpc.RpcSupport;
import cn.chloeprime.commons_impl.rpc.packet.RpcCallMethodPacket;
import cn.chloeprime.commons_impl.CommonProxy;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class RPCContext {
    public static Endpoint getSender() {
        return Objects.requireNonNull(RpcCallMethodPacket.CONTEXT.get().peek(), "RPCContext.getSender() can only be called in an RPC method!");
    }

    @Nullable
    public static ServerPlayer getSenderPlayer() {
        var sender = getSender();
        if (sender.isServer()) {
            final var msg = "Cannot invoke getSenderPlayer at client side!";
            RpcSupport.LOGGER.error(msg, new RpcException(msg));
        }
        return CommonProxy.getPlayerByUUID(sender.id()) instanceof ServerPlayer ssp ? ssp : null;
    }
}
