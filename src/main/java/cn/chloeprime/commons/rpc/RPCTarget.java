package cn.chloeprime.commons.rpc;

import cn.chloeprime.commons.rpc.exception.UnsupportedRpcOperationException;
import cn.chloeprime.commons_impl.mixin.ChunkMapAccessor;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Represents an RPC call's target.
 */
public sealed abstract class RPCTarget {
    /**
     * Client -> Server.
     *
     * @return an {@link RPCTarget} that represents the server.
     * @throws UnsupportedRpcOperationException when called on the server.
     */
    public static RPCTarget toServer() {
        checkCallingSide(LogicalSide.CLIENT);
        return SERVER.get();
    }

    /**
     * Client -> A specific client.
     *
     * @param player the server player that is running the wanted client.
     * @return an {@link RPCTarget} that represents the server player's client.
     * @throws UnsupportedRpcOperationException when called on the client.
     */
    public static RPCTarget to(@NotNull ServerPlayer player) {
        checkCallingSide(LogicalSide.SERVER);
        return new ToPlayer(player);
    }

    /**
     * Represents the caller of this RPC method.
     * Can only be called within the body of an RPC method.
     * Noop if it is called from other places or when the player has logged out.
     *
     * @throws UnsupportedRpcOperationException when called outside an RPC method.
     */
    public static RPCTarget reply() {
        if (RPCContext.getSender().isServer()) {
            return toServer();
        }
        checkCallingSide(LogicalSide.SERVER);
        var sender = RPCContext.getSenderPlayer();
        return sender != null ? to(sender) : Noop.INSTANCE;
    }

    /**
     * Represents any player that is tracking {@code center}
     *
     * @param center the center of the {@link RPCTarget}.
     * @return an {@link RPCTarget} that will send to all players tracking {@code center}, including {@code center} itself if it is a player.
     * @throws UnsupportedRpcOperationException when called on the client.
     */
    public static RPCTarget near(Entity center) {
        checkCallingSide(LogicalSide.SERVER);
        return new ToNearby(center);
    }

    /**
     * Get the target players to send to.
     */
    @ApiStatus.OverrideOnly
    public abstract Iterable<@NotNull ServerPlayer> getTarget();

    /**
     * Unused.
     *
     * @see cn.chloeprime.commons_impl.rpc.Endpoint#send(CustomPacketPayload) please use that.
     */
    @ApiStatus.OverrideOnly
    public abstract void send(CustomPacketPayload packet);

    /**
     * Checks whether the target represents a server.
     *
     * @return true if this target represents the server.
     */
    public boolean isServer() {
        return this instanceof Server;
    }

    @SuppressWarnings("Convert2MethodRef")
    private static final Supplier<RPCTarget> SERVER = Suppliers.memoize(() -> new Server());

    /**
     * Checks whether this target can be called from the following side.
     *
     * @param required the required side of this target.
     * @throws UnsupportedRpcOperationException if this target can't be called from the following side.
     */
    @ApiStatus.Internal
    public static void checkCallingSide(LogicalSide required) {
        var current = EffectiveSide.get();
        if (current == required) {
            return;
        }
        if (current.isServer()) {
            throw new UnsupportedRpcOperationException("Can't send packet from server to server");
        }
        if (current.isClient()) {
            throw new UnsupportedRpcOperationException("Can't send packet from client to client");
        }
    }

    /**
     * The server.
     *
     * @see #toServer()
     */
    public static final class Server extends RPCTarget {
        private Server() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterable<@NotNull ServerPlayer> getTarget() {
            return Collections.emptyList();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void send(CustomPacketPayload packet) {
            PacketDistributor.sendToServer(packet);
        }
    }

    /**
     * A specific player.
     *
     * @see #to(ServerPlayer)
     */
    public static final class ToPlayer extends RPCTarget {
        private final ServerPlayer player;

        public ToPlayer(@NotNull ServerPlayer player) {
            this.player = Objects.requireNonNull(player);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterable<@NotNull ServerPlayer> getTarget() {
            return List.of(player);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void send(CustomPacketPayload packet) {
            PacketDistributor.sendToPlayer(player, packet);
        }
    }

    /**
     * All players that is tracking a specific entity.
     *
     * @see #near(Entity)
     */
    public static final class ToNearby extends RPCTarget {
        private final Entity center;

        public ToNearby(@NotNull Entity center) {
            this.center = Objects.requireNonNull(center);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterable<@NotNull ServerPlayer> getTarget() {
            if (!(center.getCommandSenderWorld().getChunkSource() instanceof ServerChunkCache cache)) {
                return Collections.emptyList();
            }
            ChunkMap.TrackedEntity tracked = ((ChunkMapAccessor) cache.chunkMap).getEntityMap().get(center.getId());
            if (tracked == null) {
                return Collections.emptyList();
            }
            var seenBy = ((ChunkMapAccessor.TrackedEntity) tracked).getSeenBy();
            // Non-Null in vanilla's application
            // noinspection DataFlowIssue
            Iterable<@NotNull ServerPlayer> seenByPlayers = Iterables.transform(seenBy, ServerPlayerConnection::getPlayer);

            return center instanceof ServerPlayer me
                    ? Iterables.concat(seenByPlayers, List.of(me))
                    : seenByPlayers;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void send(CustomPacketPayload packet) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(center, packet);
        }
    }

    /**
     * Fallback implementation.
     */
    public static final class Noop extends RPCTarget {
        public static final Noop INSTANCE = new Noop();

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterable<@NotNull ServerPlayer> getTarget() {
            return Collections.emptyList();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void send(CustomPacketPayload packet) {
            // Noop
        }

        private Noop() {
        }
    }
}
