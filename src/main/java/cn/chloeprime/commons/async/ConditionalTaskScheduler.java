package cn.chloeprime.commons.async;

import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * A wrapper that adds extra condition to task calls.
 *
 * @param condition the condition.
 * @param delegate the base scheduler.
 * @author ChloePrime
 */
record ConditionalTaskScheduler(
        BooleanSupplier condition,
        TaskScheduler delegate
) implements TaskScheduler {
    @Override
    public Task delay(int ticks, @NotNull Consumer<Task> callback) {
        return delegate.delay(ticks, task -> {
            if (condition.getAsBoolean()) {
                callback.accept(task);
            }
        });
    }

    @Override
    public Task repeat(int ticks, @NotNull Consumer<Task> callback) {
        return delegate.repeat(ticks, task -> {
            if (condition.getAsBoolean()) {
                callback.accept(task);
            } else {
                task.stop();
            }
        });
    }

    @Override
    public Task countdown(int time, int count, @NotNull Consumer<Task> callback) {
        return delegate.countdown(time, count, task -> {
            if (condition.getAsBoolean()) {
                callback.accept(task);
            } else {
                task.stop();
            }
        });
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException();
    }
}
