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
     *
     * @return the allowed network direction of this remote callable method
     */
    @Nullable RPCFlow flow() default RPCFlow.BIDIRECTIONAL;

    /**
     * Whether the method will be called locally when called through the RPC system
     * <p>
     * If you set this to true,
     * Please remember to check {@link RPCContext#isCalledThroughRPC()} before calling {@link RPCContext#getSender()}
     *
     * @return if true the method will be called locally when called through the RPC system.
     * @see RPCContext#isCalledThroughRPC() the method to check whether the current context is called locally or remotely.
     * @since 2001.2.0.5, 2101.2.0.5
     */
    boolean callLocally() default false;
}
