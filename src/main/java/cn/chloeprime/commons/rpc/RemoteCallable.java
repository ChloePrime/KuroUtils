package cn.chloeprime.commons.rpc;

import cn.chloeprime.commons.rpc.exception.CallToNonRpcMethodException;
import cn.chloeprime.commons.rpc.exception.WrongRPCFlowException;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate on a method to make it remote callable via {@link RPC#call}.
 * If using {@link RPC#call} on a method without this annotation or on the wrong side (see {@link #flow()}),
 * a {@link CallToNonRpcMethodException} will be thrown or logged.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RemoteCallable {
    /**
     * The allowed network direction of this remote callable method.
     * If called on the wrong side, a {@link WrongRPCFlowException} will be thrown or logged.
     */
    @Nullable RPCFlow flow() default RPCFlow.BIDIRECTIONAL;
}
