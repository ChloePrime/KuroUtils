package cn.chloeprime.commons.rpc.exception;

@SuppressWarnings("unused")
public class RpcException extends RuntimeException {
    @java.io.Serial
    private static final long serialVersionUID = 5214527086436941368L;

    public RpcException() {
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
