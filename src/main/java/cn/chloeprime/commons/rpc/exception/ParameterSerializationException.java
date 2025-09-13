package cn.chloeprime.commons.rpc.exception;

public class ParameterSerializationException extends RpcException {
    public ParameterSerializationException() {
        super();
    }

    public ParameterSerializationException(String message) {
        super(message);
    }

    public ParameterSerializationException(Throwable cause) {
        super(cause);
    }
}
