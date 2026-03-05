package cn.chloeprime.commons_impl.mixin;

import cn.chloeprime.commons_impl.rpg.EnhancedDamageSource;
import com.google.common.collect.Sets;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;
import java.util.Set;

@Mixin(DamageSource.class)
public class MixinDamageSource implements EnhancedDamageSource {
    private @Unique @Nullable Set<TagKey<DamageType>> kuroutils$injectedAllowTags;
    private @Unique @Nullable Set<TagKey<DamageType>> kuroutils$injectedDenyTags;

    @ModifyReturnValue(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At("RETURN"))
    private boolean perObjectIs(boolean original, TagKey<DamageType> candidate) {
        return (original
                || (kuroutils$injectedAllowTags != null && kuroutils$injectedAllowTags.contains(candidate)))
                && (kuroutils$injectedDenyTags == null || !kuroutils$injectedDenyTags.contains(candidate));
    }

    @Override
    public void kuroutils$injectIs(TagKey<DamageType> tag) {
        if (kuroutils$injectedAllowTags == null) {
            kuroutils$injectedAllowTags = Sets.newIdentityHashSet();
        }
        kuroutils$injectedAllowTags.add(tag);
    }

    @Override
    public void kuroutils$injectIs(Collection<TagKey<DamageType>> tags) {
        if (kuroutils$injectedAllowTags == null) {
            kuroutils$injectedAllowTags = Sets.newIdentityHashSet();
        }
        kuroutils$injectedAllowTags.addAll(tags);
    }

    @Override
    public void kuroutils$injectIsNot(TagKey<DamageType> tag) {
        if (kuroutils$injectedDenyTags == null) {
            kuroutils$injectedDenyTags = Sets.newIdentityHashSet();
        }
        kuroutils$injectedDenyTags.add(tag);
    }

    @Override
    public void kuroutils$injectIsNot(Collection<TagKey<DamageType>> tags) {
        if (kuroutils$injectedDenyTags == null) {
            kuroutils$injectedDenyTags = Sets.newIdentityHashSet();
        }
        kuroutils$injectedDenyTags.addAll(tags);
    }
}
