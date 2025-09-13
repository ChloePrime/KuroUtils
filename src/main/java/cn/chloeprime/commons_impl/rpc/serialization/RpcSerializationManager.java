package cn.chloeprime.commons_impl.rpc.serialization;

import cn.chloeprime.commons.rpc.exception.ParameterSerializationException;
import cn.chloeprime.commons_impl.rpc.LambdaReflectResult;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.Map;
import java.util.Objects;

public class RpcSerializationManager {
    @SuppressWarnings("unchecked")
    public static void write(RegistryFriendlyByteBuf buf, LambdaReflectResult lambda, Object[] arguments) throws UnsupportedOperationException {
        Class<?>[] parameterTypes = lambda.handle().type().parameterArray();
        for (int i = 0; i < parameterTypes.length; i++) {
            var parameterType = WRAPPER_TO_PRIMITIVE.getOrDefault(parameterTypes[i], parameterTypes[i]);
            getSerializerFor(parameterType).encode(buf, arguments[i]);
        }
    }

    public static Object[] read(RegistryFriendlyByteBuf buf, LambdaReflectResult lambda) {
        Class<?>[] parameterTypes = lambda.handle().type().parameterArray();
        var parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameters.length; i++) {
            var parameterType = WRAPPER_TO_PRIMITIVE.getOrDefault(parameterTypes[i], parameterTypes[i]);
            parameters[i] = getSerializerFor(parameterType).decode(buf);
        }
        return parameters;
    }

    private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE = Map.of(
            Boolean.class, boolean.class,
            Byte.class, byte.class,
            Short.class, short.class,
            Character.class, char.class,
            Integer.class, int.class,
            Long.class, long.class,
            Float.class, float.class,
            Double.class, double.class
    );

    @SuppressWarnings("rawtypes")
    private static <T> RpcParameterSerializer getSerializerFor(Class<? extends T> argType) {
        var result = (Class<?>) Objects.requireNonNull(argType);
        while (result != null) {
            var serializer = RpcSerializers.BY_TYPE.get(result);
            if (serializer != null) {
                return serializer;
            }
            result = result.getSuperclass();
        }
        if (argType.isArray()) {
            return getSerializerFor(argType.componentType()).arrayType();
        }
        throw new ParameterSerializationException("Unsupported parameter type: " + argType.getCanonicalName());
    }
}
