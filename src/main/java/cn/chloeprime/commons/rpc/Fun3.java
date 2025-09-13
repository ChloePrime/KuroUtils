package cn.chloeprime.commons.rpc;

import java.io.Serializable;

/**
 * Remote callable function with 3 arguments
 */
@FunctionalInterface
public interface Fun3<T1, T2, T3> extends Serializable {
    /**
     * Call this function locally.
     * Use {@link RPC#call(RPCTarget, Fun3, Object, Object, Object)} to call it on the remote.
     */
    void call(T1 arg1, T2 arg2, T3 arg3);
}
