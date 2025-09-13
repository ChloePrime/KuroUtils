package cn.chloeprime.commons_impl.rpc;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents an endpoint.
 * An endpoint is either the server, or a player's client.
 *
 * @param id the uuid of the player, zero uuid for the server.
 */
public record Endpoint(
        UUID id
) {
    /**
     * The server with a zero {@link #id}.
     */
    public static final Endpoint SERVER = new Endpoint(new UUID(0, 0));

    /**
     * Check whether this endpoint represents the server.
     *
     * @return whether this endpoint represents the server.
     */
    public boolean isServer() {
        return SERVER.id().equals(this.id());
    }

    /**
     * Get an endpoint that represents the {@code serverPlayer}'s client
     *
     * @param serverPlayer the server player running the client.
     * @return an endpoint representing the player's client.
     */
    public static Endpoint forPlayer(@NotNull ServerPlayer serverPlayer) {
        Objects.requireNonNull(serverPlayer);
        return new Endpoint(serverPlayer.getUUID());
    }

    /**
     * Sends a packet to the endpoint.
     * Can't send to the local machine, otherwise an {@link IllegalStateException} will be thrown.
     *
     * @param packet the packet to send. its type should be registered to the mod loader's network system.
     * @throws IllegalStateException when trying to send a packet to the local machine.
     */
    public void send(CustomPacketPayload packet) {
        var side = EffectiveSide.get();
        if (side.isClient() != this.isServer()) {
            var msg = side.isServer()
                    ? "Can't sent to remote \"local machine\" on the server side!"
                    : "Can't sent to other players from client side!";
            throw new IllegalStateException(msg);
        }
        if (side.isClient()) {
            PacketDistributor.sendToServer(packet);
        }
        if (side.isServer()) {
            var id = Objects.requireNonNull(this.id());
            var server = Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer(), "Server is not found on the server side?");
            if (server.overworld().getPlayerByUUID(id) instanceof ServerPlayer player) {
                PacketDistributor.sendToPlayer(player, packet);
            }
        }
    }
}
