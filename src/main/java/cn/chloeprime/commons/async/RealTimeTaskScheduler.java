package cn.chloeprime.commons.async;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Task impl based on {@link System#currentTimeMillis}
 *
 * @author ChloePrime
 */
public class RealTimeTaskScheduler extends AbstractTaskScheduler {
    public RealTimeTaskScheduler() {
        super(true);
    }

    private long now;

    @Override
    public void update() {
        now = System.currentTimeMillis();
        super.update();
    }

    /**
     * 以系统时间作为当前时间。
     *
     * @return 系统时间，以毫秒为单位
     */
    @Override
    protected long getTickCount() {
        return now;
    }

    @Override
    protected TaskItem newTaskItem(int delay, @Nonnull Consumer<Task> callback, int count) {
        // 将 sec 减少半 tick，以防止由于毫秒数和 tick 数 * 50 相差甚小，导致延时容易浮动 1 tick。
        return new TaskItem(getTickCount() - 25, delay, callback, count);
    }
}
