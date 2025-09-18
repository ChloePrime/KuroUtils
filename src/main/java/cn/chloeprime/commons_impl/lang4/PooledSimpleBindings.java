package cn.chloeprime.commons_impl.lang4;

import cn.chloeprime.commons.lang4.Formula;
import org.jetbrains.annotations.VisibleForTesting;

import javax.script.SimpleBindings;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PooledSimpleBindings extends SimpleBindings implements Formula.PooledBindings {
    public static final int MAX_SIZE = 65536;
    private static final Queue<PooledSimpleBindings> POOL = new ArrayDeque<>(256);
    private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();

    public static PooledSimpleBindings get() {
        final var lock = LOCK.readLock();
        try {
            lock.lock();
            return Optional.ofNullable(POOL.poll()).orElseGet(PooledSimpleBindings::new);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() {
        final var lock = LOCK.writeLock();
        try {
            lock.lock();
            if (POOL.size() < MAX_SIZE) {
                this.clear();
                POOL.offer(this);
            }
        } finally {
            lock.unlock();
        }
    }

    @VisibleForTesting
    public static int getPooledObjectCount() {
        final var lock = LOCK.readLock();
        try {
            lock.lock();
            return POOL.size();
        } finally {
            lock.unlock();
        }
    }
}
