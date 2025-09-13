package cn.chloeprime.commons_impl.xver;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/**
 * 方便同步到其他 MC 版本时统一更改
 */
public class CrossVersionHelper {
    public static ResourceLocation identifier(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }

    public static Level getLevel(Entity entity) {
        return entity.level();
    }
}
