package cn.chloeprime.commons.rpc;

import java.io.Serializable;

/**
 * Remote callable function with 4 arguments
 */
@FunctionalInterface
public interface Fun4<T1, T2, T3, T4> extends Serializable {
    /**
     * Call this function locally.
     * Use {@link RPC#call(RPCTarget, Fun4, Object, Object, Object, Object)} to call it on the remote.
     */
    void call(T1 arg1, T2 arg2, T3 arg3, T4 arg4);
}
