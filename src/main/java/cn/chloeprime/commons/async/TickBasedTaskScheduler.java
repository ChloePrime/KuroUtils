package cn.chloeprime.commons.async;

import net.minecraftforge.fml.LogicalSide;

/**
 * Task impl based on game ticks / manual update.
 * @author ChloePrime
 */
public class TickBasedTaskScheduler extends AbstractTaskScheduler {
    public TickBasedTaskScheduler(boolean autoUpdate, LogicalSide side) {
        super(autoUpdate, side);
    }

    private long ticks = 0L;

    @Override
    public void update() {
        ++ticks;
        super.update();
    }

    @Override
    protected long getTickCount() {
        return ticks;
    }
}
