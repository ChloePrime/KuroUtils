package cn.chloeprime.commons.rpc;

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
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public sealed abstract class RPCTarget {
    /**
     * Client -> Server
     */
    public static RPCTarget toServer() {
        checkCallingSide(LogicalSide.CLIENT);
        return SERVER.get();
    }

    public static RPCTarget to(@NotNull ServerPlayer player) {
        checkCallingSide(LogicalSide.SERVER);
        return new ToPlayer(player);
    }

    public static RPCTarget reply() {
        if (EffectiveSide.get().isClient()) {
            return toServer();
        }
        checkCallingSide(LogicalSide.SERVER);
        var sender = RPCContext.getSenderPlayer();
        return sender != null ? to(sender) : Noop.INSTANCE;
    }

    public static RPCTarget near(Entity center) {
        checkCallingSide(LogicalSide.SERVER);
        return new ToNearby(center);
    }

    public abstract Iterable<@NotNull ServerPlayer> getTarget();
    public abstract void send(CustomPacketPayload packet);

    public boolean isServer() {
        return this instanceof Server;
    }

    @SuppressWarnings("Convert2MethodRef")
    private static final Supplier<RPCTarget> SERVER = Suppliers.memoize(() -> new Server());

    public static void checkCallingSide(LogicalSide required) {
        var current = EffectiveSide.get();
        if (current == required) {
            return;
        }
        if (current.isServer()) {
            throw new UnsupportedOperationException("Can't send packet from server to server");
        }
        if (current.isClient()) {
            throw new UnsupportedOperationException("Can't send packet from client to client");
        }
    }

    public static final class Server extends RPCTarget {
        private Server() {
        }

        @Override
        public Iterable<@NotNull ServerPlayer> getTarget() {
            return Collections.emptyList();
        }

        @Override
        public void send(CustomPacketPayload packet) {
            PacketDistributor.sendToServer(packet);
        }
    }

    public static final class ToPlayer extends RPCTarget {
        private final ServerPlayer player;

        public ToPlayer(@NotNull ServerPlayer player) {
            this.player = Objects.requireNonNull(player);
        }

        @Override
        public Iterable<@NotNull ServerPlayer> getTarget() {
            return List.of(player);
        }

        @Override
        public void send(CustomPacketPayload packet) {
            PacketDistributor.sendToPlayer(player, packet);
        }
    }

    public static final class ToNearby extends RPCTarget {
        private final Entity center;

        private ToNearby(@NotNull Entity center) {
            this.center = Objects.requireNonNull(center);
        }

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

        @Override
        public void send(CustomPacketPayload packet) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(center, packet);
        }
    }

    public static final class Noop extends RPCTarget {
        public static final Noop INSTANCE = new Noop();

        @Override
        public Iterable<@NotNull ServerPlayer> getTarget() {
            return Collections.emptyList();
        }

        @Override
        public void send(CustomPacketPayload packet) {
            // Noop
        }

        private Noop() {
        }
    }
}
