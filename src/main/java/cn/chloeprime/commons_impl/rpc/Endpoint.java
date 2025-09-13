package cn.chloeprime.commons_impl.rpc;

import cn.chloeprime.commons_impl.network.KUNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;
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

    public void send(Object packet) {
        var side = EffectiveSide.get();
        if (side.isClient() != this.isServer()) {
            var msg = side.isServer()
                    ? "Can't sent to remote \"local machine\" on the server side!"
                    : "Can't sent to other players from client side!";
            throw new IllegalStateException(msg);
        }
        if (side.isClient()) {
            KUNetwork.CHANNEL.sendToServer(packet);
        }
        if (side.isServer()) {
            var id = Objects.requireNonNull(this.id());
            if (ServerLifecycleHooks.getCurrentServer().overworld().getPlayerByUUID(id) instanceof ServerPlayer player) {
                KUNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
            }
        }
    }
}
