package cn.chloeprime.commons_impl.rpc.serialization;

import org.jetbrains.annotations.VisibleForTesting;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class RpcSerializationUtils {
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
