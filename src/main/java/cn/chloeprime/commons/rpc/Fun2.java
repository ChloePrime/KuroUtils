package cn.chloeprime.commons.rpc;

import java.io.Serializable;
import java.util.function.BiConsumer;

/**
 * Remote callable function with 2 arguments
 */
@FunctionalInterface
public interface Fun2<T1, T2> extends Serializable, BiConsumer<T1, T2> {
    /**
     * Call this function locally.
     * Use {@link RPC#call(RPCTarget, Fun2, Object, Object)} to call it on the remote.
     */
    void call(T1 arg1, T2 arg2);

    /**
     * Adapter for {@link BiConsumer}
     */
    @Override
    default void accept(T1 arg1, T2 arg2) {
        call(arg1, arg2);
    }
}
