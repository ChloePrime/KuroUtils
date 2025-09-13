package cn.chloeprime.commons_impl;

import cn.chloeprime.commons_impl.rpc.Endpoint;
import cn.chloeprime.commons_impl.mixin.client.ClientLevelAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

@EventBusSubscriber(Dist.CLIENT)
public class ClientProxy {
    private static final Minecraft MC = Minecraft.getInstance();
    private static RegistryAccess fallbackRegistryAccess;

    public static Optional<RegistryAccess> getRegistryAccess() {
        var result = Optional.ofNullable(MC.level).map(Level::registryAccess).orElse(fallbackRegistryAccess);
        return Optional.ofNullable(result);
    }

    @SubscribeEvent
    public static void updateFallbackRegistryAccessOnResourceReload(AddReloadListenerEvent event) {
        fallbackRegistryAccess = event.getRegistryAccess();
    }

    public static @NotNull Endpoint getLocalEndpoint() {
        return new Endpoint(MC.getUser().getProfileId());
    }

    public static @Nullable Entity getEntityByUUID(UUID uuid) {
        return Optional.ofNullable(MC.level)
                .map(level -> ((ClientLevelAccessor) level).callGetEntities().get(uuid))
                .orElse(null);
    }

    public static @Nullable Player getPlayerByUUID(UUID uuid) {
        return Optional.ofNullable(MC.level)
                .map(level -> level.getPlayerByUUID(uuid))
                .orElse(null);
    }

    public static @Nullable Entity getEntityByID(int id) {
        return Optional.ofNullable(MC.level)
                .map(level -> level.getEntity(id))
                .orElse(null);
    }
}
