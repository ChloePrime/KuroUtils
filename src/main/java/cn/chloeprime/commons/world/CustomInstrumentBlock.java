package cn.chloeprime.commons.world;

import cn.chloeprime.commons_impl.mixin.example.CustomInstrumentBlockExample;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import java.util.Optional;

/**
 * Blocks that implement this interface can customize
 * sound event for note block.
 * <p>
 * The expected place position is controlled by
 * {@link NoteBlockInstrument#worksAboveNoteBlock()} of your block's
 * vanilla instrument type.
 *
 * @see CustomInstrumentBlockExample for an example usage.
 * @since 2.3.0
 * @author ChloePrime
 */
public interface CustomInstrumentBlock {
    Optional<Holder<SoundEvent>> getNoteBlockSound(Level level, BlockPos noteBlockPos, BlockState noteBlock);
}
