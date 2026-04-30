package cn.chloeprime.commons.lang4;

import java.util.concurrent.locks.Lock;

/**
 * A lock guard, that locks the lock when constructed, and unlocks when closed.
 *
 * @param lock the lock to operate with
 * @see Locks#lock
 * @since 4.11.0
 */
public record LockGuard(Lock lock) implements AutoCloseable {
    public LockGuard {
        lock.lock();
    }

    @Override
    public void close() {
        lock.unlock();
    }
}
