package cn.chloeprime.commons_impl.rpc.serialization;

import cn.chloeprime.commons.rpc.exception.ParameterSerializationException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class RpcSerializationUtils {
    public static final String WRAPPED_VALUE_KEY = "__value__";

    public static <T> void encodeByCodec(Codec<T> codec, FriendlyByteBuf buf, T value) {
        // T -> Tag
        var result = codec.encodeStart(NbtOps.INSTANCE, value).get();
        result.ifRight(error -> {
            throw new ParameterSerializationException(error.message());
        });
        Tag tag = result.left().orElseThrow(ParameterSerializationException::new);
        try {
            // Tag -> CompoundTag
            CompoundTag compound;
            if (tag instanceof CompoundTag alreadyCompound) {
                compound = alreadyCompound;
            } else {
                compound = new CompoundTag();
                compound.put(WRAPPED_VALUE_KEY, tag);
            }
            // CompoundTag -> ByteBuf
            var output = new ByteArrayOutputStream();
            NbtIo.writeCompressed(compound, output);
            buf.writeByteArray(output.toByteArray());
        } catch (IOException ex) {
            throw new ParameterSerializationException(ex);
        }
    }

    public static <T> T decodeByCodec(Codec<T> codec, FriendlyByteBuf buf) {
        // ByteBuf -> CompoundTag
        CompoundTag compound;
        try {
            var raw = buf.readByteArray();
            compound = NbtIo.readCompressed(new ByteArrayInputStream(raw));
        } catch (IOException ex) {
            throw new ParameterSerializationException(ex);
        }
        // CompoundTag -> Tag
        Tag realTag;
        if (compound.contains(WRAPPED_VALUE_KEY)) {
            realTag = compound.get(WRAPPED_VALUE_KEY);
        } else {
            realTag = compound;
        }
        // Tag -> T
        var result = codec.decode(NbtOps.INSTANCE, realTag).get();
        result.ifRight(error -> {
            throw new ParameterSerializationException(error.message());
        });
        return result.left().map(Pair::getFirst).orElseThrow(ParameterSerializationException::new);
    }

    @VisibleForTesting
    public static Class<?> findCommonParentClasses(Iterable<?> registry) {
        Class<?> result = null;
        Set<Class<?>> interfaces = new LinkedHashSet<>();
        for (Object object : registry) {
            if (result == null) {
                result = object.getClass();
                if (result.isRecord()) {
                    return result;
                }
                while (result != null && (result.isAnonymousClass() || result.isSynthetic())) {
                    var result2 = result;
                    interfaces.addAll(Arrays.asList(result.getInterfaces()));
                    interfaces.removeIf(i -> !(i.isAssignableFrom(result2)));
                    result = result.getSuperclass();
                }
                continue;
            }
            var result2 = result;
            while (!result.isAssignableFrom(object.getClass()) && result != Object.class) {
                interfaces.addAll(Arrays.asList(result.getInterfaces()));
                interfaces.removeIf(i -> !(i.isAssignableFrom(result2)));
                result = result.getSuperclass();
            }
        }
        if (result != null && result != Object.class) {
            return result;
        }
        return interfaces.size() == 1 ? interfaces.iterator().next() : null;
    }
}
