package cn.chloeprime.commons_impl.mixin;

import cn.chloeprime.commons_impl.world.CustomInstrumentBlockHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author ChloePrime
 */
@Mixin(NoteBlock.class)
public class MixinNoteBlock {
    @ModifyExpressionValue(
            method = "triggerEvent",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/properties/NoteBlockInstrument;getSoundEvent()Lnet/minecraft/core/Holder;"))
    private Holder<SoundEvent> getCustomSoundIdFromInterface(
            Holder<SoundEvent> original,
            BlockState noteBlock, Level level, BlockPos pos
    ) {
        return CustomInstrumentBlockHandler.getNoteBlockSoundId(original, noteBlock, level, pos);
    }
}
