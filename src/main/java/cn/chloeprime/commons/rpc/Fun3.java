package cn.chloeprime.commons.rpc;

import java.io.Serializable;

@FunctionalInterface
public interface Fun3<T1, T2, T3> extends Serializable {
    void call(T1 arg1, T2 arg2, T3 arg3);
}
