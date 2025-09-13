package cn.chloeprime.commons_impl.rpc.serialization;

import cn.chloeprime.commons.rpc.exception.ParameterSerializationException;
import cn.chloeprime.commons_impl.rpc.LambdaReflectResult;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    private static final ThreadLocal<Deque<Set<RpcParameterSerializer<?>>>> INTERFACE_RESULTS_OBJECT_POOL = ThreadLocal.withInitial(ArrayDeque::new);
    private static final Map<Class<?>, RpcParameterSerializer<?>> CLASS_TO_SERIALIZER = new ConcurrentHashMap<>();

    @SuppressWarnings("rawtypes")
    private static <T> RpcParameterSerializer getSerializerFor(Class<? extends T> argType) {
        return CLASS_TO_SERIALIZER.computeIfAbsent(argType, RpcSerializationManager::getSerializerFor0);
    }

    @SuppressWarnings("rawtypes")
    private static <T> RpcParameterSerializer getSerializerFor0(Class<? extends T> argType) {
        var clazz = (Class<?>) Objects.requireNonNull(argType);
        var setPool = INTERFACE_RESULTS_OBJECT_POOL.get();
        Set<RpcParameterSerializer<?>> interfaceResults = Objects.requireNonNullElseGet(setPool.poll(), LinkedHashSet::new);
        try {
            while (clazz != null) {
                var serializer = RpcSerializers.BY_TYPE.get(clazz);
                if (serializer != null) {
                    return serializer;
                }
                // 处理可被序列化的接口
                // 如果本类直接实现的接口发现序列化器，那么remove上一次计算（它的子类）的结果
                var thisClassHasInterfaceResult = false;
                for (Class<?> i : clazz.getInterfaces()){
                    var interfaceSerializer = RpcSerializers.BY_TYPE.get(i);
                    if (interfaceSerializer != null) {
                        if (!thisClassHasInterfaceResult) {
                            thisClassHasInterfaceResult = true;
                            interfaceResults.clear();
                        }
                        interfaceResults.add(interfaceSerializer);
                    }
                }
                clazz = clazz.getSuperclass();
            }
            if (argType.isArray()) {
                return getSerializerFor(argType.componentType()).arrayType();
            }
            if (interfaceResults.size() == 1) {
                return interfaceResults.iterator().next();
            }
            if (interfaceResults.size() > 1) {
                throw new ParameterSerializationException("Unsupported parameter type: %s, it has more than one (%d) serializable interfaces".formatted(
                        argType.getCanonicalName(), interfaceResults.size()
                ));
            } else {
                throw new ParameterSerializationException("Unsupported parameter type: " + argType.getCanonicalName());
            }
        } finally {
            interfaceResults.clear();
            setPool.push(interfaceResults);
        }
    }
}
