package cn.chloeprime.commons_impl.rpg;

import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import java.util.Collection;

public interface EnhancedDamageSource {
    void kuroutils$injectIs(TagKey<DamageType> tag);
    void kuroutils$injectIs(Collection<TagKey<DamageType>> tags);
    void kuroutils$injectIsNot(TagKey<DamageType> tag);
    void kuroutils$injectIsNot(Collection<TagKey<DamageType>> tags);
}
