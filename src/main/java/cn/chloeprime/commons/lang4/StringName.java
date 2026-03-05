package cn.chloeprime.commons.lang4;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.Codec;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;

/**
 * An interned string.
 * Slower to construct, but faster to compare equality and hash.
 *
 * @param value the content of this string name.
 * @author ChloePrime
 */
@JsonAdapter(StringName.JsonAdapter.class)
@SuppressWarnings("StringEquality")
public record StringName(
        @Nonnull String value
) {
    public static final Codec<StringName> CODEC = Codec.STRING.xmap(StringName::of, StringName::value);

    public StringName {
        Objects.requireNonNull(value);
        value = value.intern();
    }

    /**
     * Factory variant of themain constructor
     *
     * @param str the content of this string name.
     * @return a new StringName instance that represents param {@param str}.
     * @author ChloePrime
     */
    public static StringName of(String str) {
        return new StringName(str);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (StringName) obj;
        return this.value == that.value;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(value) ^ 879416385;
    }

    @Override
    public @Nonnull String toString() {
        return value;
    }

    /**
     * JSON type adapter of StringName.
     * Copied and modified from {@link com.google.gson.internal.bind.TypeAdapters#STRING}.
     */
    public static class JsonAdapter extends TypeAdapter<StringName> {
        @Override
        public StringName read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            /* coerce booleans to strings for backwards compatibility */
            if (peek == JsonToken.BOOLEAN) {
                return StringName.of(Boolean.toString(in.nextBoolean()));
            }
            return StringName.of(in.nextString());
        }

        @Override
        public void write(JsonWriter out, StringName value) throws IOException {
            out.value(value.value());
        }
    }
}
