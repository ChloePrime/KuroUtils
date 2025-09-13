package cn.chloeprime.commons.rpc;

import cn.chloeprime.commons.rpc.exception.RpcException;
import cn.chloeprime.commons_impl.rpc.Endpoint;
import cn.chloeprime.commons_impl.rpc.RpcSupport;
import cn.chloeprime.commons_impl.rpc.packet.RpcCallMethodPacket;
import cn.chloeprime.commons_impl.CommonProxy;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Context of an RPC call.
 */
public class RPCContext {
    /**
     * Get the caller of the RPC call.
     *
     * @return the caller of the RPC call.
     */
    public static Endpoint getSender() {
        return Objects.requireNonNull(RpcCallMethodPacket.CONTEXT.get().peek(), "RPCContext.getSender() can only be called in an RPC method!");
    }

    /**
     * Get the caller of the RPC call as a server player.
     *
     * @return the caller of the RPC call, {@code null} if this call is called from the server.
     */
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
