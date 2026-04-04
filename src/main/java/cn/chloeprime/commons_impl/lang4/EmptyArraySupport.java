package cn.chloeprime.commons_impl.lang4;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;

public final class EmptyArraySupport {
    public static final ClassValue<Object> EMPTY_ARRAY_MAP = new ClassValue<>() {
        @Override
        protected Object computeValue(@NotNull Class<?> type) {
            return Array.newInstance(type, 0);
        }
    };

    private EmptyArraySupport() {}
}
