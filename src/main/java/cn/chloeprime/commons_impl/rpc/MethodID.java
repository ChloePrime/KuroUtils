package cn.chloeprime.commons_impl.rpc;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

public record MethodID(int value) {
    public static final MethodID ZERO = new MethodID(0);
    public static final Int2ObjectMap<LambdaReflectResult> KNOWN_IDS = new Int2ObjectLinkedOpenHashMap<>();

    public static MethodID of(int value) {
        return new MethodID(value);
    }
}
