package cn.chloeprime.commons.rpc.exception;

public class CallToNonRpcMethodException extends RpcException {
    public CallToNonRpcMethodException(String msg) {
        super(msg);
    }
}
