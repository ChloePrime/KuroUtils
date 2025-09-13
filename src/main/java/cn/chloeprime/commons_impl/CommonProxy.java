package cn.chloeprime.commons_impl;

import cn.chloeprime.commons_impl.rpc.Endpoint;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class CommonProxy {
    public static boolean IS_DEDICATED_SERVER = FMLLoader.getDist().isDedicatedServer();

    public static Optional<RegistryAccess> getRegistryAccess() {
        if (IS_DEDICATED_SERVER || EffectiveSide.get().isServer()) {
            return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer()).map(MinecraftServer::registryAccess);
        } else {
            return ClientProxy.getRegistryAccess();
        }
    }

    public static @NotNull Endpoint getLocalEndpoint() {
        if (IS_DEDICATED_SERVER || EffectiveSide.get().isServer()) {
            return Endpoint.SERVER;
        } else {
            return ClientProxy.getLocalEndpoint();
        }
    }

    public static @Nullable Entity getEntityByID(int id) {
        if (IS_DEDICATED_SERVER || EffectiveSide.get().isServer()) {
            return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer())
                    .map(server -> server.overworld().getEntity(id))
                    .orElse(null);
        } else {
            return ClientProxy.getEntityByID(id);
        }
    }

    public static @Nullable Entity getEntityByUUID(UUID uuid) {
        if (IS_DEDICATED_SERVER || EffectiveSide.get().isServer()) {
            return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer())
                    .map(server -> server.overworld().getEntity(uuid))
                    .orElse(null);
        } else {
            return ClientProxy.getEntityByUUID(uuid);
        }
    }

    public static @Nullable Player getPlayerByUUID(UUID uuid) {
        if (IS_DEDICATED_SERVER || EffectiveSide.get().isServer()) {
            return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer())
                    .map(server -> server.overworld().getPlayerByUUID(uuid))
                    .orElse(null);
        } else {
            return ClientProxy.getPlayerByUUID(uuid);
        }
    }
}
