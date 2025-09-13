package cn.chloeprime.commons.rpc.exception;

/**
 * Thrown when an unsupported operation occurs in the RPC system.
 */
public class UnsupportedRpcOperationException extends RpcException {
    public UnsupportedRpcOperationException(String message) {
        super(message);
    }
}
