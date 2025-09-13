package cn.chloeprime.commons.rpc;

import java.io.Serializable;

/**
 * Remote callable function with 7 arguments
 */
@FunctionalInterface
public interface Fun7<T1, T2, T3, T4, T5, T6, T7> extends Serializable {
    /**
     * Call this function locally.
     * Use {@link RPC#call(RPCTarget, Fun7, Object, Object, Object, Object, Object, Object, Object)} to call it on the remote.
     */
    void call(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7);
}
