package cn.chloeprime.commons_impl.rpc.serialization;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.lang.reflect.Array;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

public interface RpcParameterSerializer<T> {
    Class<T> getBaseClass();
    void encode(RegistryFriendlyByteBuf buf, T value);
    T decode(RegistryFriendlyByteBuf buf);

    /**
     * Constructs a serializer for a specific type.
     *
     * @param <T> The type of the object to be serialized
     * @return a serializer of type {@link T}
     */
    static <T> RpcParameterSerializer<T> of(
            Class<T> baseClass,
            BiConsumer<RegistryFriendlyByteBuf, T> encoder,
            Function<RegistryFriendlyByteBuf, T> decoder
    ) {
        return new RpcParameterSerializer<>() {
            @Override
            public Class<T> getBaseClass() {
                return baseClass;
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, T value) {
                encoder.accept(buf, value);
            }

            @Override
            public T decode(RegistryFriendlyByteBuf buf) {
                return decoder.apply(buf);
            }
        };
    }

    /**
     * Constructs a serializer for a specific type of array.
     * Suitable for primitive arrays.
     *
     * @param <A> Must be an array type
     * @return a serializer of type {@link A}
     */
    static <A> RpcParameterSerializer<A> ofArray(
            Class<A> arrayType,
            IntFunction<A> constructor,
            ToIntFunction<A> lengthGetter,
            BiConsumer<RegistryFriendlyByteBuf, A> encoder,
            BiConsumer<RegistryFriendlyByteBuf, A> decoder
    ) {
        return new RpcParameterSerializer<>() {
            @Override
            public Class<A> getBaseClass() {
                return arrayType;
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, A array) {
                buf.writeVarInt(lengthGetter.applyAsInt(array));
                encoder.accept(buf, array);
            }

            @Override
            public A decode(RegistryFriendlyByteBuf buf) {
                var array = constructor.apply(buf.readVarInt());
                decoder.accept(buf, array);
                return array;
            }
        };
    }

    /**
     * Constructs a serializer for an array of {@link T}.
     * Suitable for object arrays.
     *
     * @param <T> The element type of the array
     * @return a serializer of type {@link T[]}
     */
    @SuppressWarnings("unchecked")
    static <T> RpcParameterSerializer<T[]> arrayOf(
            Class<T> elementClass,
            BiConsumer<RegistryFriendlyByteBuf, T> elementEncoder,
            Function<RegistryFriendlyByteBuf, T> elementDecoder
    ) {
        var arrayClass = (Class<T[]>) elementClass.arrayType();
        return ofArray(
                arrayClass,
                i -> (T[]) Array.newInstance(elementClass, i),
                arr -> arr.length,
                (buf, array) -> {
                    for (T t : array) {
                        elementEncoder.accept(buf, t);
                    }
                },
                (buf, array) -> {
                    for (int i = 0; i < array.length; i++) {
                        array[i] = elementDecoder.apply(buf);
                    }
                }
        );
    }

    static <T> RpcParameterSerializer<T> of(Class<T> type, Codec<T> codec) {
        return of(type, ByteBufCodecs.fromCodec(codec));
    }

    /**
     * @since 2101.2.0.0
     */
    static <T> RpcParameterSerializer<T> of(Class<T> type , StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        return of(type, codec::encode, codec::decode);
    }

    default RpcParameterSerializer<T[]> arrayType() {
        return arrayOf(getBaseClass(), this::encode, this::decode);
    }

    default <R> RpcParameterSerializer<R> transform(
            Class<R> targetClass,
            Function<T, R> to,
            Function<R, T> from
    ) {
        return new RpcParameterSerializer<>() {
            @Override
            public Class<R> getBaseClass() {
                return targetClass;
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, R value) {
                RpcParameterSerializer.this.encode(buf, from.apply(value));
            }

            @Override
            public R decode(RegistryFriendlyByteBuf buf) {
                return to.apply(RpcParameterSerializer.this.decode(buf));
            }
        };
    }
}
