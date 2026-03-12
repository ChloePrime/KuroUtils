package cn.chloeprime.commons_impl.world;

import cn.chloeprime.commons.world.CustomInstrumentBlock;
import cn.chloeprime.commons.world.event.NoteBlockGetSoundEventEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author ChloePrime
 */
public final class CustomInstrumentBlockHandler {
    public static Holder<SoundEvent> getNoteBlockSoundId(
            Holder<SoundEvent> original,
            BlockState noteBlock, Level level, BlockPos pos
    ) {
        var above = noteBlock.getValue(NoteBlock.INSTRUMENT).worksAboveNoteBlock();
        var samplerBlock = level.getBlockState(above ? pos.above() : pos.below());
        if (samplerBlock.getBlock() instanceof CustomInstrumentBlock block) {
            var instrument = block.getNoteBlockSound(level, pos, noteBlock).orElse(null);
            if (instrument != null) {
                original = instrument;
            }
        }
        var event = new NoteBlockGetSoundEventEvent(level, pos, noteBlock, noteBlock.getValue(NoteBlock.NOTE), original);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getSoundEvent();
    }
}
