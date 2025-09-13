package cn.chloeprime.commons.rpc.exception;

/**
 * Thrown when calling a method on the wrong direction.
 */
public class WrongRPCFlowException extends RpcException {
    public WrongRPCFlowException() {
        this("Wrong packet flow");
    }

    public WrongRPCFlowException(String message) {
        super(message);
    }
}
