package cn.chloeprime.commons_impl.mixin.example;

import cn.chloeprime.commons.world.CustomInstrumentBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

/**
 * An example usage of {@link CustomInstrumentBlock}
 *
 * @author ChloePrime
 */
@Mixin({ScaffoldingBlock.class, SkullBlock.class})
@SuppressWarnings({"unused", "UnusedMixin"})
public class CustomInstrumentBlockExample implements CustomInstrumentBlock {
    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public Optional<Holder<SoundEvent>> getNoteBlockSound(Level level, BlockPos noteBlockPos, BlockState noteBlock) {
        return Optional.of(Holder.direct(SoundEvents.ZOMBIE_ATTACK_IRON_DOOR));
    }
}
