package cn.chloeprime.commons.world.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.NoteBlockEvent;

/**
 * Event to customize sound event for note blocks.
 *
 * @author ChloePrime
 */
public class NoteBlockGetSoundEventEvent extends NoteBlockEvent {
    private final Holder<SoundEvent> original;
    private Holder<SoundEvent> current;

    public NoteBlockGetSoundEventEvent(
            Level level, BlockPos pos, BlockState state, int note,
            Holder<SoundEvent> original
    ) {
        super(level, pos, state, note);
        this.original = this.current = original;
    }

    /**
     * Get the original sound event.
     *
     * @return the original sound event before modification of this event.
     */
    public Holder<SoundEvent> getOriginalSoundEvent() {
        return original;
    }

    /**
     * Get current sound event.
     *
     * @return the current sound event to be played.
     */
    public Holder<SoundEvent> getSoundEvent() {
        return current;
    }

    /**
     * Set the sound event to be played.
     *
     * @param newEvent the new sound event to be played.
     */
    public void setSoundEvent(Holder<SoundEvent> newEvent) {
        this.current = newEvent;
    }
}
