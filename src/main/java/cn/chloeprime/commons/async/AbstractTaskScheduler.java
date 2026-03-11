package cn.chloeprime.commons.async;

import com.google.common.base.Preconditions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;

/**
 * Task impl with auto update possibility.
 *
 * @author zat, ChloePrime
 */
@EventBusSubscriber
public abstract class AbstractTaskScheduler implements TaskScheduler {
    private static long getTimeOfArrival(TaskItem item) {
        return item.timeOfArrival;
    }

    private static final Logger LOGGER = LogManager.getLogger();

    private final boolean autoUpdate;
    private final LogicalSide logicSide;
    private final PriorityQueue<TaskItem> items = new PriorityQueue<>(
            Comparator.comparingLong(AbstractTaskScheduler::getTimeOfArrival)
    );

    public AbstractTaskScheduler(boolean autoUpdate, LogicalSide side) {
        this.autoUpdate = autoUpdate;
        this.logicSide = side;

        if (!this.autoUpdate) {
            return;
        }

        if (isTicking(logicSide)) {
            throw new ConcurrentModificationException(
                    "Instantiating another AbstractTask() during a task callback is not allowed"
            );
        }

        getInstancesForSide(this.logicSide).add(new WeakReference<>(this));
    }

    @Override
    public TaskItem delay(int ticks, @Nonnull Consumer<Task> callback) {
        Preconditions.checkNotNull(callback);
        return newTaskItem(ticks, callback, 1);
    }

    @Override
    public TaskItem repeat(int delay, @Nonnull Consumer<Task> callback) {
        Preconditions.checkNotNull(callback);
        // Prevents infinite loop.
        if (delay <= 0) {
            throw new IllegalArgumentException("the delay of AbstractTask.repeat must be greater than zero.");
        }
        return newTaskItem(delay, callback, TaskItem.REPEAT);
    }

    @Override
    public TaskItem countdown(int time, int count, @Nonnull Consumer<Task> callback) {
        Preconditions.checkNotNull(callback);
        return newTaskItem(time, callback, count);
    }

    protected TaskItem newTaskItem(int delay, @Nonnull Consumer<Task> callback, int count) {
        return new TaskItem(getTickCount(), delay, callback, count);
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public void update() {
        while (true) {
            TaskItem top = items.peek();
            if (top == null || top.timeOfArrival > getTickCount()) {
                return;
            }

            if (items.poll() != top) {
                throw new ConcurrentModificationException("getTickCount() changed the TaskItem list");
            }
            top.nextLoop();
            if (!top.isTerminated()) {
                items.offer(top);
            }
        }
    }

    /**
     * 获取当前时间。
     * 此处时间是个抽象的概念，可以用GameTick实现，也可以用真实时间/手动更新次数等实现。
     *
     * @return 当前时间
     */
    protected abstract long getTickCount();

    public class TaskItem implements Task {
        private final int delay;
        private final Consumer<Task> callback;
        /**
         * -2持续
         * -1
         */
        private int count;
        private long timeOfArrival;
        private static final int REPEAT = -1;

        /**
         * @param count -1时持续
         */
        protected TaskItem(long startTime, int delay, Consumer<Task> callback, int count) {
            this.delay = delay;
            this.callback = callback;
            this.count = count;
            timeOfArrival = startTime + delay;

            items.add(this);
        }

        @Override
        public void stop() {
            this.count = 0;
        }

        private void nextLoop() {
            runCallback();
            if (this.count == 0) {
                return;
            }
            timeOfArrival += delay;
            if (this.count > 0) {
                --this.count;
            }
        }

        /**
         * 执行回调，如果遇到报错，则输出到日志。
         */
        private void runCallback() {
            try {
                this.callback.accept(this);
            } catch (OutOfMemoryError oom) {
                throw oom;
            } catch (Throwable ex) {
                LOGGER.error("A task generated an exception", ex);
                stop();
            }
        }

        private boolean isTerminated() {
            return this.count == 0;
        }
    }

    /* 每刻自动更新 */

    private static final List<WeakReference<AbstractTaskScheduler>> AUTO_UPDATE_SERVER = new LinkedList<>();
    private static final List<WeakReference<AbstractTaskScheduler>> AUTO_UPDATE_CLIENT = new LinkedList<>();
    private static volatile boolean clientTicking;
    private static volatile boolean serverTicking;

    private static List<WeakReference<AbstractTaskScheduler>> getInstancesForSide(LogicalSide side) {
        return side.isClient() ? AUTO_UPDATE_CLIENT : AUTO_UPDATE_SERVER;
    }

    private boolean isOnRightSide(LogicalSide sideIn) {
        return this.logicSide == sideIn;
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Pre event) {
        onTick(LogicalSide.SERVER);
    }

    public static void onClientTick() {
        onTick(LogicalSide.CLIENT);
    }

    private static void onTick(LogicalSide currentSide) {
        Iterator<WeakReference<AbstractTaskScheduler>> ite = getInstancesForSide(currentSide).iterator();

        markTicking(currentSide, true);
        while (ite.hasNext()) {
            AbstractTaskScheduler task = ite.next().get();
            if (task == null || !task.autoUpdate || !task.isOnRightSide(currentSide)) {
                ite.remove();
                continue;
            }
            task.update();
        }
        markTicking(currentSide, false);
    }

    private static boolean isTicking(LogicalSide side) {
        return side.isClient() ? clientTicking : serverTicking;
    }

    private static void markTicking(LogicalSide side, boolean value) {
        if (side.isClient()) {
            clientTicking = value;
        } else {
            serverTicking = value;
        }
    }
}
