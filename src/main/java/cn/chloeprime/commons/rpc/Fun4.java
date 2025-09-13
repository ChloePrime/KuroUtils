package cn.chloeprime.commons.rpc;

import java.io.Serializable;

@FunctionalInterface
public interface Fun4<T1, T2, T3, T4> extends Serializable {
    void call(T1 arg1, T2 arg2, T3 arg3, T4 arg4);
}
