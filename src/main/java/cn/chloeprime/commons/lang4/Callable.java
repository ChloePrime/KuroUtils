package cn.chloeprime.commons.lang4;

import java.util.function.Supplier;

/**
 * Safe callable, can be called without throwing,
 * Has a better name that is better at representing subroutines.
 *
 * @param <V> Type of return value.
 */
@FunctionalInterface
public interface Callable<V> extends java.util.concurrent.Callable<V>, Supplier<V> {
    /**
     * {@inheritDoc}
     */
    V call();

    @Override
    default V get() {
        return call();
    }
}