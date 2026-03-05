package cn.chloeprime.commons_impl;

import cn.chloeprime.commons_impl.rpc.Endpoint;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.server.ServerLifecycleHooks;
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
                    .map(server -> getEntityById0(server, id))
                    .orElse(null);
        } else {
            return ClientProxy.getEntityByID(id);
        }
    }

    public static int getEntityID(@Nullable Entity entity) {
        return entity != null ? entity.getId() : 0;
    }

    private static @Nullable Entity getEntityById0(MinecraftServer server, int id) {
        for (var level : server.getAllLevels()) {
            var entity = level.getEntity(id);
            if (entity != null) {
                return entity;
            }
        }
        return null;
    }

    public static @Nullable Entity getEntityByUUID(UUID uuid) {
        if (IS_DEDICATED_SERVER || EffectiveSide.get().isServer()) {
            return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer())
                    .map(server -> getEntityByUUID0(server, uuid))
                    .orElse(null);
        } else {
            return ClientProxy.getEntityByUUID(uuid);
        }
    }

    private static @Nullable Entity getEntityByUUID0(MinecraftServer server, UUID uid) {
        var player = server.getPlayerList().getPlayer(uid);
        if (player != null) {
            return player;
        }
        for (var level : server.getAllLevels()) {
            var entity = level.getEntity(uid);
            if (entity != null) {
                return entity;
            }
        }
        return null;
    }

    public static @Nullable Player getPlayerByUUID(UUID uuid) {
        if (IS_DEDICATED_SERVER || EffectiveSide.get().isServer()) {
            return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer())
                    .map(server -> server.getPlayerList().getPlayer(uuid))
                    .orElse(null);
        } else {
            return ClientProxy.getPlayerByUUID(uuid);
        }
    }
}
