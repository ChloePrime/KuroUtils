package cn.chloeprime.commons.rpc;

import cn.chloeprime.commons_impl.rpc.RpcSupport;

public class RPC {
    public static
    void call(RPCTarget target, Fun0 method) {
        RpcSupport.call(target, method);
    }

    public static <T1>
    void call(RPCTarget target, Fun1<T1> method, T1 arg1) {
        RpcSupport.call(target, method, arg1);
    }

    public static <T1, T2>
    void call(RPCTarget target, Fun2<T1, T2> method, T1 arg1, T2 arg2) {
        RpcSupport.call(target, method, arg1, arg2);
    }

    public static <T1, T2, T3>
    void call(RPCTarget target, Fun3<T1, T2, T3> method, T1 arg1, T2 arg2, T3 arg3) {
        RpcSupport.call(target, method, arg1, arg2, arg3);
    }

    public static <T1, T2, T3, T4>
    void call(RPCTarget target, Fun4<T1, T2, T3, T4> method, T1 arg1, T2 arg2, T3 arg3, T4 arg4) {
        RpcSupport.call(target, method, arg1, arg2, arg3, arg4);
    }

    public static <T1, T2, T3, T4, T5>
    void call(
            RPCTarget target,
            Fun5<T1, T2, T3, T4, T5> method,
            T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5
    ) {
        RpcSupport.call(target, method, arg1, arg2, arg3, arg4, arg5);
    }

    public static <T1, T2, T3, T4, T5, T6>
    void call(
            RPCTarget target,
            Fun6<T1, T2, T3, T4, T5, T6> method,
            T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6
    ) {
        RpcSupport.call(target, method, arg1, arg2, arg3, arg4, arg5, arg6);
    }

    public static <T1, T2, T3, T4, T5, T6, T7>
    void call(
            RPCTarget target,
            Fun7<T1, T2, T3, T4, T5, T6, T7> method,
            T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7
    ) {
        RpcSupport.call(target, method, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8>
    void call(
            RPCTarget target,
            Fun8<T1, T2, T3, T4, T5, T6, T7, T8> method,
            T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7, T8 arg8
    ) {
        RpcSupport.call(target, method, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
    }
}
