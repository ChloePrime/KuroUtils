package cn.chloeprime.commons.async;

/**
 * Task impl based on game ticks / manual update.
 * @author ChloePrime
 */
public class TickBasedTaskScheduler extends AbstractTaskScheduler {
    /**
     * autoUpdate = true
     */
    public TickBasedTaskScheduler() {
        super();
    }

    public TickBasedTaskScheduler(boolean autoUpdate) {
        super(autoUpdate);
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
