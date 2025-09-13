package cn.chloeprime.commons.lang4;

import java.util.Objects;

/**
 * Float version of {@link java.util.function.DoubleConsumer}
 */
@FunctionalInterface
@SuppressWarnings("unused")
public interface FloatConsumer {
    void accept(float value);

    default FloatConsumer andThen(FloatConsumer after) {
        Objects.requireNonNull(after);
        return f -> {
            accept(f);
            after.accept(f);
        };
    }
}
