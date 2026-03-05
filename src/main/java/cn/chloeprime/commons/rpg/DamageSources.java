package cn.chloeprime.commons.rpg;

import cn.chloeprime.commons_impl.rpg.EnhancedDamageSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

import java.util.Collection;

/**
 * Damage source manipulation.
 */
public class DamageSources {
    /**
     * Enforce a {@link DamageSource} to be considered as a specific tag.
     *
     * @param source The damage source object.
     * @param tag the tag to be enforced considered as.
     */
    public static void injectIs(DamageSource source, TagKey<DamageType> tag) {
        ((EnhancedDamageSource) source).kuroutils$injectIs(tag);
    }

    /**
     * Enforce a {@link DamageSource} to be considered as specific tags.
     *
     * @param source The damage source object.
     * @param tags the tags to be enforced considered as.
     */
    public static void injectIs(DamageSource source, Collection<TagKey<DamageType>> tags) {
        ((EnhancedDamageSource) source).kuroutils$injectIs(tags);
    }

    /**
     * Enforce a {@link DamageSource} to be considered as not a specific tag.
     * This method has a higher priority than {@link #injectIs(DamageSource, TagKey)}
     *
     * @param source The damage source object.
     * @param tag the tag to be enforced considered as not.
     */
    public static void injectIsNot(DamageSource source, TagKey<DamageType> tag) {
        ((EnhancedDamageSource) source).kuroutils$injectIsNot(tag);
    }

    /**
     * Enforce a {@link DamageSource} to be considered as not specific tags.
     * This method has a higher priority than {@link #injectIs(DamageSource, Collection)}
     *
     * @param source The damage source object.
     * @param tags the tags to be enforced considered as not.
     */
    public static void injectIsNot(DamageSource source, Collection<TagKey<DamageType>> tags) {
        ((EnhancedDamageSource) source).kuroutils$injectIsNot(tags);
    }
}
