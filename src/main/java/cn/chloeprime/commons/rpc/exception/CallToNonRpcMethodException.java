package cn.chloeprime.commons.rpc.exception;

import cn.chloeprime.commons.rpc.RemoteCallable;

/**
 * Thrown when calling a method that is not annotated with {@link RemoteCallable} through the RPC system.
 */
public class CallToNonRpcMethodException extends RpcException {
    public CallToNonRpcMethodException(String msg) {
        super(msg);
    }
}
