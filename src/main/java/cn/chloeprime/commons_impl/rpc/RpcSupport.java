package cn.chloeprime.commons_impl.rpc;

import cn.chloeprime.commons.ContextUtil;
import cn.chloeprime.commons.rpc.RPCFlow;
import cn.chloeprime.commons.rpc.exception.CallToNonRpcMethodException;
import cn.chloeprime.commons.rpc.exception.UnsupportedRpcOperationException;
import cn.chloeprime.commons.rpc.exception.WrongRPCFlowException;
import cn.chloeprime.commons_impl.KuroUtilsMod;
import cn.chloeprime.commons.rpc.RPCTarget;
import cn.chloeprime.commons.rpc.RemoteCallable;
import cn.chloeprime.commons_impl.rpc.packet.RpcCallMethodPacket;
import com.google.common.collect.Iterables;
import com.mojang.logging.LogUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.lang.invoke.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class RpcSupport {
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<Object, LambdaReflectResult> LAMBDA_TO_METHOD_CACHE = new WeakHashMap<>();

    /**
     * A marker for failed method reflect attempts,
     * to prevent log spams.
     */
    private static final LambdaReflectResult BAD_METHOD = new LambdaReflectResult(null, null, new MutableObject<>(), RpcSupport.class.getName(), "Bad Method", "()V");

    private static final ClassLoader CLASS_LOADER = KuroUtilsMod.class.getClassLoader();
    private static final MethodHandles.Lookup MY_LOOKUP = MethodHandles.lookup();
    private static final ClassValue<MethodHandles.Lookup> LOOKUPS = new ClassValue<>() {
        @Override
        protected MethodHandles.Lookup computeValue(@NotNull Class<?> type) {
            try {
                return MethodHandles.privateLookupIn(type, MY_LOOKUP);
            } catch (IllegalAccessException ex) {
                LOGGER.error("Failed to create method handle lookup for {}", type.getSimpleName(), ex);
                return null;
            }
        }
    };

    public static void call(RPCTarget target, Object lambda, Object... args) {
        var method = reflect(lambda).orElse(null);
        if (method == null) {
            return;
        }
        RemoteCallable metadata = Objects.requireNonNull(method.method().getAnnotation(RemoteCallable.class));
        // Collect endpoints
        Iterable<Endpoint> targetsEndpoints;
        if (target.isServer()) {
            targetsEndpoints = List.of(Endpoint.SERVER);
        } else {
            targetsEndpoints = Iterables.transform(target.getTarget(), Endpoint::forPlayer);
        }
        var sender = ContextUtil.getLocalEndpoint();
        if (metadata.callLocally()) {
            var locallyStack = RpcCallMethodPacket.LOCAL_CALL_CONTEXT.get();
            try {
                locallyStack.push(true);
                invoke(method.handle(), args);
            } finally {
                locallyStack.pop();
            }
        }
        for (var targetEndpoint : targetsEndpoints) {
            // Ensure remote knows MethodID
            if (!validateRpcCall(method, targetEndpoint)) {
                continue;
            }
            var id = MethodKnowledgeDatabase.ensureRemoteKnowledge(targetEndpoint, method);
            targetEndpoint.send(new RpcCallMethodPacket(id, sender, method, args));
        }
    }

    public static void invoke(MethodHandle handle, Object[] arguments) {
        try {
            handle.invokeWithArguments(arguments);
        } catch (Throwable ex) {
            RpcSupport.LOGGER.error("Failed to invoke RPC method", ex);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean validateRpcCall(LambdaReflectResult method, Endpoint target) {
        var anno = method.method().getAnnotation(RemoteCallable.class);
        if (anno == null) {
            return false;
        }
        var flow = anno.flow();
        if (flow != RPCFlow.BIDIRECTIONAL && (flow == RPCFlow.CLIENT_TO_SERVER) != target.isServer()) {
            var actualFlow = target.isServer() ? RPCFlow.CLIENT_TO_SERVER : RPCFlow.SERVER_TO_CLIENT;
            LOGGER.error("Wrong RPC flow: Requires {}, Actual: {}", flow, actualFlow, new WrongRPCFlowException());
            return false;
        }
        return true;
    }

    public static Optional<LambdaReflectResult> recreateKnowledgeFromPacket(
            String className,
            String methodName,
            String methodSignature
    ) throws ReflectiveOperationException {
        var clazz = Class.forName(className);
        var lookup = LOOKUPS.get(clazz);
        if (lookup == null) {
            return Optional.empty();
        }
        var methodType = MethodType.fromMethodDescriptorString(methodSignature, CLASS_LOADER);
        var method = clazz.getDeclaredMethod(methodName, methodType.parameterArray());
        var methodHandle = lookup.unreflect(method);
        return Optional.of(new LambdaReflectResult(method, methodHandle, new MutableObject<>(), className, methodName, methodSignature));
    }

    private static Optional<LambdaReflectResult> reflect(Object lambda) {
        LambdaReflectResult result;
        synchronized (LAMBDA_TO_METHOD_CACHE) {
            var cached = LAMBDA_TO_METHOD_CACHE.get(lambda);
            if (cached != null) {
                result = cached;
            } else {
                try {
                    var calculated = reflect0(lambda).orElse(null);
                    result = (calculated != null && validateRpcMethod(calculated.method())) ? calculated : BAD_METHOD;
                } catch (ReflectiveOperationException exception) {
                    KuroUtilsMod.LOGGER.error("Failed to do convert remote function to method handle", exception);
                    result = BAD_METHOD;
                }
                LAMBDA_TO_METHOD_CACHE.put(lambda, result);
            }
        }
        return BAD_METHOD.equals(result) ? Optional.empty() : Optional.of(result);
    }

    private static Optional<LambdaReflectResult> reflect0(Object lambda) throws ReflectiveOperationException {
        SerializedLambda serialized = serialize(lambda);
        var className = serialized.getImplClass().replace('/', '.');
        var methodName = serialized.getImplMethodName();
        var methodSignature = serialized.getImplMethodSignature();
        return recreateKnowledgeFromPacket(className, methodName, methodSignature)
                .filter(result -> validateRpcMethod(result.method()));
    }

    private static SerializedLambda serialize(Object lambda) {
        var clazz = lambda.getClass();
        while (clazz != null && clazz != Object.class) {
            try {
                var writeReplace = clazz.getDeclaredMethod("writeReplace");
                writeReplace.setAccessible(true);

                if (writeReplace.invoke(lambda) instanceof SerializedLambda serialized) {
                    return serialized;
                }
                break;
            } catch (NoSuchMethodException ignored) {
                clazz = clazz.getSuperclass();
            } catch (IllegalAccessException | InvocationTargetException e) {
                break;
            }
        }
        throw new UnsupportedOperationException("Unserializable Lambda");
    }

    public static boolean validateRpcMethod(Method method) {
        if (!Modifier.isStatic(method.getModifiers())) {
            final var msg = "RPC method should be static method";
            LOGGER.error(msg, new UnsupportedRpcOperationException(msg));
        }
        var anno = method.getAnnotation(RemoteCallable.class);
        if (anno == null) {
            final var msg = "RPC methods should be annotated with @RemoteCallable";
            LOGGER.error(msg, new CallToNonRpcMethodException(msg));
            return false;
        }
        return true;
    }
}
