package cn.chloeprime.commons_impl.rpc;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public record Endpoint(
        UUID id
) {
    public static final Endpoint SERVER = new Endpoint(new UUID(0, 0));

    public boolean isServer() {
        return SERVER.id().equals(this.id());
    }

    public static Endpoint forPlayer(@NotNull ServerPlayer serverPlayer) {
        Objects.requireNonNull(serverPlayer);
        return new Endpoint(serverPlayer.getUUID());
    }

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
