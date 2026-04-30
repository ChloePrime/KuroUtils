package cn.chloeprime.commons.lang4;

import cn.chloeprime.commons_impl.lang4.ExceptionHelper;
import org.apache.commons.lang3.concurrent.locks.LockingVisitors;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Supersede {@link LockingVisitors} with short names :P
 */
@SuppressWarnings("unused")
public final class Locks {
    /**
     * A runnable that may need some 'correction'
     */
    @FunctionalInterface
    public interface FailableRunnable {
        void run() throws Throwable;
    }

    /**
     * A supplier that may need some 'correction'
     *
     * @param <T> the type of the constructed result
     */
    @FunctionalInterface
    public interface FailableSupplier<T> {
        T get() throws Throwable;
    }

    /**
     * RAII styled lock usage, Use with {@code try with resources}.
     *
     * @param lock the lock to operate with
     * @return an RAII lock guard that unlocks the lock when closed
     */
    public static LockGuard lock(Lock lock) {
        return new LockGuard(lock);
    }

    /**
     * Lock with the given lock and run something.
     *
     * @param lock the lock object
     * @param code the code to wrap
     */
    public static void runLocked(Lock lock, FailableRunnable code) {
        try (var guard = lock(lock)) {
            code.run();
        } catch (Throwable ex) {
            throw ExceptionHelper.sneak(ex);
        }
    }

    /**
     * Lock with the given lock and supply something.
     *
     * @param lock the lock object
     * @param code the code to wrap
     * @param <T>  the type of the execution result
     * @return the execution result of {@code code}
     */
    public static <T> T getLocked(Lock lock, FailableSupplier<T> code) {
        try (var guard = lock(lock)) {
            return code.get();
        } catch (Throwable ex) {
            throw ExceptionHelper.sneak(ex);
        }
    }

    /**
     * An alias of {@link #getRead}.
     *
     * @param lock the lock object
     * @param code the read code to wrap
     * @param <T>  the type of the execution result
     * @return the execution result of {@code code}
     */
    public static <T> T read(ReadWriteLock lock, FailableSupplier<T> code) {
        return getRead(lock, code);
    }

    /**
     * An alias of {@link #runWrite}.
     *
     * @param lock the lock object
     * @param code the write code to wrap
     */
    public static void write(ReadWriteLock lock, FailableRunnable code) {
        runWrite(lock, code);
    }

    /**
     * Lock with the read lock and supply something.
     *
     * @param lock the lock object
     * @param code the read code to wrap
     * @param <T>  the type of the execution result
     * @return the execution result of {@code code}
     */
    public static <T> T getRead(ReadWriteLock lock, FailableSupplier<T> code) {
        return getLocked(lock.readLock(), code);
    }

    /**
     * Lock with the write lock and supply something.
     *
     * @param lock the lock object
     * @param code the write code to wrap
     * @param <T>  the type of the execution result
     * @return the execution result of {@code code}
     */
    public static <T> T getWrite(ReadWriteLock lock, FailableSupplier<T> code) {
        return getLocked(lock.writeLock(), code);
    }

    /**
     * Lock with the read lock and run something.
     *
     * @param lock the lock object
     * @param code the code to wrap
     */
    public static void runRead(ReadWriteLock lock, FailableRunnable code) {
        runLocked(lock.readLock(), code);
    }


    /**
     * Lock with the write lock and run something.
     *
     * @param lock the lock object
     * @param code the code to wrap
     */
    public static void runWrite(ReadWriteLock lock, FailableRunnable code) {
        runLocked(lock.writeLock(), code);
    }

    private Locks() {
    }
}
