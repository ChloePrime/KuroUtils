package cn.chloeprime.commons.async;

import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * @author zat, ChloePrime
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface TaskScheduler {

    // Factory Methods

    /**
     * Create a task scheduler that is based on game tick.
     * The time unit of the returned scheduler is **tick**.
     *
     * @return a task scheduler that is based on game tick.
     */
    static TaskScheduler createTickBased(LogicalSide tickSide) {
        return new TickBasedTaskScheduler(true, tickSide);
    }

    /**
     * Create a task scheduler that is based on real time.
     * The time unit of the returned scheduler is **milliseconds**.
     * <p>
     * The task is scheduled to the given event loop (client or server tick)
     * after the given real time delay.
     *
     * @param eventLoopSide The side used to get the event loop.
     * @return a task scheduler that is based on game tick.
     */
    static TaskScheduler createRealTimeBased(LogicalSide eventLoopSide) {
        return new RealTimeTaskScheduler(eventLoopSide);
    }

    /**
     * Create a task scheduler that is based on explicit calls to {@link #update()}.
     * The time unit of the returned scheduler is call times to {@link #update()}.
     *
     * @return a task scheduler that is based on game tick.
     */
    static TaskScheduler createManuallyUpdated() {
        // Side does matter when autoUpdate is false
        return new TickBasedTaskScheduler(false, LogicalSide.SERVER);
    }

    // Regular Scheduling APIs

    /**
     * Run the callback after some ticks
     *
     * @param ticks    Delay amount (unit depends on implementation)
     * @param callback called when time passed
     * @return the task handle, which can stop the task
     */
    Task delay(int ticks, @Nonnull Consumer<Task> callback);

    /**
     * Run the callback repeatedly with the given interval
     *
     * @param ticks    Delay amount (unit depends on implementation)
     * @param callback called repeatedly
     * @return the task handle, which can stop the task
     */
    Task repeat(int ticks, @Nonnull Consumer<Task> callback);

    /**
     * Run the callback repeatedly with given times and given interval
     *
     * @param time     Delay amount (unit depends on implementation)
     * @param count    Repeat Count
     * @param callback Called repeatedly with given times
     * @return the task handle, which can stop the task
     */
    Task countdown(int time, int count, @Nonnull Consumer<Task> callback);

    /**
     * Return a conditional task scheduler wrapper,
     * that wraps APIs
     *
     * @param condition the extra condition.
     * @return A wrapped scheduler, that only executes tasks when condition is true.
     */
    default TaskScheduler withCondition(BooleanSupplier condition) {
        return new ConditionalTaskScheduler(condition, this);
    }

    // CompletableFuture Styled APIs

    /**
     * Returns a {@link CompletableFuture} that get completed after given delay
     *
     * @param time  Delay amount (unit depends on implementation)
     * @return A future that completes after given delay.
     */
    default CompletableFuture<Void> delay(int time) {
        var future = new CompletableFuture<Void>();
        delay(time, _i -> future.complete(null));
        return future;
    }

    /**
     * Run the callback after given delay and get the value,
     * using the {@link CompletableFuture} introduced in Java8
     *
     * @param ticks    Delay amount (unit depends on implementation)
     * @param callback called when time passed
     * @param <R> Type of return value
     * @return a {@link CompletableFuture} that completes synchronized after the given delay
     */
    default <R> CompletableFuture<R> runDelayed(int ticks, @Nonnull Callable<R> callback) {
        CompletableFuture<R> future = new CompletableFuture<>();
        delay(ticks, ignored -> {
            R ret;
            try {
                ret = callback.call();
            } catch (OutOfMemoryError oom) {
                throw oom;
            } catch (Throwable ex) {
                future.completeExceptionally(ex);
                return;
            }
            // Don't catch exception in thenXXX(code) added by callers
            future.complete(ret);
        });
        return future;
    }

    // Scheduler Management APIs

    /**
     * Clear all scheduled tasks
     */
    void clear();

    /**
     * Whether the task object is empty (has no task items)
     *
     * @return Whether the task object has no task items
     */
    boolean isEmpty();

    /**
     * Updates all tasks' countdown
     * <p>
     * Tasks created by default constructor will update once a tick.
     * While some tasks may be updated manually.
     */
    void update();
}
