package cn.chloeprime.commons.rpc;

import java.io.Serializable;
import java.util.function.BiConsumer;

@FunctionalInterface
public interface Fun2<T1, T2> extends Serializable, BiConsumer<T1, T2> {
    void call(T1 arg1, T2 arg2);

    @Override
    default void accept(T1 arg1, T2 arg2) {
        call(arg1, arg2);
    }
}
