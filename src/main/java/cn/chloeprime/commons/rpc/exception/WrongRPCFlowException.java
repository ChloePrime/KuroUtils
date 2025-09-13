package cn.chloeprime.commons.rpc.exception;

public class WrongRPCFlowException extends RpcException {
    public WrongRPCFlowException() {
        this("Wrong packet flow");
    }

    public WrongRPCFlowException(String message) {
        super(message);
    }
}
