package cn.chloeprime.commons.rpc;

import java.io.Serializable;

/**
 * Remote callable function with 0 arguments
 */
@FunctionalInterface
public interface Fun0 extends Serializable, Runnable {
    /**
     * Call this function locally.
     * Use {@link RPC#call(RPCTarget, Fun0)} to call it on the remote.
     */
    void call();

    /**
     * Adapter for {@link Runnable}
     */
    @Override
    default void run() {
        call();
    }
}
