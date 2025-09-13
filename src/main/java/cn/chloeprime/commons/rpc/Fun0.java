package cn.chloeprime.commons.rpc;

import java.io.Serializable;

@FunctionalInterface
public interface Fun0 extends Serializable, Runnable {
    void call();

    @Override
    default void run() {
        call();
    }
}
