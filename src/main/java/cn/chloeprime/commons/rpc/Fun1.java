package cn.chloeprime.commons.rpc;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Remote callable function with 1 argument
 */
@FunctionalInterface
public interface Fun1<T1> extends Serializable, Consumer<T1> {
    /**
     * Call this function locally.
     * Use {@link RPC#call(RPCTarget, Fun1, Object)} to call it on the remote.
     */
    void call(T1 arg1);

    /**
     * Adapter for {@link Consumer}
     */
    @Override
    default void accept(T1 arg1) {
        call(arg1);
    }
}
