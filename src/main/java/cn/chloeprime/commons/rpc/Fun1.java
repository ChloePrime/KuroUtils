package cn.chloeprime.commons.rpc;

import java.io.Serializable;
import java.util.function.Consumer;

@FunctionalInterface
public interface Fun1<T1> extends Serializable, Consumer<T1> {
    void call(T1 arg1);

    @Override
    default void accept(T1 arg1) {
        call(arg1);
    }
}
