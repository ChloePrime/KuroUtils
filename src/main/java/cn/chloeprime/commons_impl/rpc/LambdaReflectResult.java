package cn.chloeprime.commons_impl.rpc;

import org.apache.commons.lang3.mutable.MutableObject;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.Objects;

public record LambdaReflectResult(
        Method method,
        MethodHandle handle,
        MutableObject<Object> thisArg,
        String className,
        String methodName,
        String methodSignature
) {
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LambdaReflectResult other)) {
            return false;
        }
        return Objects.equals(this.method, other.method);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(method);
    }
}
