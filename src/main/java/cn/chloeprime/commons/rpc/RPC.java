package cn.chloeprime.commons.rpc;

import cn.chloeprime.commons_impl.rpc.RpcSupport;
import cn.chloeprime.commons_impl.rpc.serialization.RpcParameterSerializer;
import cn.chloeprime.commons_impl.rpc.serialization.RpcSerializers;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Main API for the RPC system.
 * <p>
 * <h2>Method requirement</h2>
 * In order for a method to be successfully called on the remote side, the method
 * should satisfy the following conditions:
 * <ul>
 * <li>An RPC method should be a static method annotated with {@link RemoteCallable}.
 * Private methods are supported as long as they aren't strongly encapsulated using the Java module system.</li>
 * <li>Every parameter should have a type or a supertype that has a corresponded {@link RpcParameterSerializer}.
 * See {@link RpcSerializers} for built-in serializers.
 * You can register your own {@link RpcParameterSerializer}s using {@link RpcSerializers#REGISTRY_KEY}
 * and the registry system.</li>
 * <li>Registry entries, like {@link Block}s, {@link Item}s, {@link EntityType}s,
 * have an automatically generated {@link RpcParameterSerializer}, thus are not needed to be registered manually.</li>
 * </ul>
 *
 * <h2>Performance note</h2>
 * Basic data types (primitive, numbers, JDK offered types) cost the same bytes as their in-memory format.
 * <p>
 * For integers and long integers, the var int mechanism is used (see {@link FriendlyByteBuf#writeVarInt(int)} and {@link FriendlyByteBuf#writeVarLong(long)}),
 * and uses 1~5 bytes for integers, 1~9 bytes for long integers.
 * The bigger the value is, the more bytes it will consume.
 * <p>
 * Cost for registry entries is the same as an integer if the registry is synced,
 * elsewise it will cost the same bytes as a {@link ResourceLocation}
 * <p>
 * Cost for anything that is serialized by a {@link Codec} is the same as encoding it with {@link NbtOps#INSTANCE}
 * and then G-Zipping it.
 */
public class RPC {
    /**
     * Call a method with 0 arguments on the remote target.
     * @param target the remote target(s) that should invoke the method.
     * @param method the method to call, should be a static method. No lambdas or instance methods.
     */
    public static
    void call(RPCTarget target, Fun0 method) {
        RpcSupport.call(target, method);
    }

    /**
     * Call a method with 1 argument on the remote target.
     * @param target the remote target(s) that should invoke the method.
     * @param method the method to call, should be a static method. No lambdas or instance methods.
     */
    public static <T1>
    void call(RPCTarget target, Fun1<T1> method, T1 arg1) {
        RpcSupport.call(target, method, arg1);
    }

    /**
     * Call a method with 2 arguments on the remote target.
     * @param target the remote target(s) that should invoke the method.
     * @param method the method to call, should be a static method. No lambdas or instance methods.
     */
    public static <T1, T2>
    void call(RPCTarget target, Fun2<T1, T2> method, T1 arg1, T2 arg2) {
        RpcSupport.call(target, method, arg1, arg2);
    }

    /**
     * Call a method with 3 arguments on the remote target.
     * @param target the remote target(s) that should invoke the method.
     * @param method the method to call, should be a static method. No lambdas or instance methods.
     */
    public static <T1, T2, T3>
    void call(RPCTarget target, Fun3<T1, T2, T3> method, T1 arg1, T2 arg2, T3 arg3) {
        RpcSupport.call(target, method, arg1, arg2, arg3);
    }

    /**
     * Call a method with 4 arguments on the remote target.
     * @param target the remote target(s) that should invoke the method.
     * @param method the method to call, should be a static method. No lambdas or instance methods.
     */
    public static <T1, T2, T3, T4>
    void call(RPCTarget target, Fun4<T1, T2, T3, T4> method, T1 arg1, T2 arg2, T3 arg3, T4 arg4) {
        RpcSupport.call(target, method, arg1, arg2, arg3, arg4);
    }

    /**
     * Call a method with 5 arguments on the remote target.
     * @param target the remote target(s) that should invoke the method.
     * @param method the method to call, should be a static method. No lambdas or instance methods.
     */
    public static <T1, T2, T3, T4, T5>
    void call(
            RPCTarget target,
            Fun5<T1, T2, T3, T4, T5> method,
            T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5
    ) {
        RpcSupport.call(target, method, arg1, arg2, arg3, arg4, arg5);
    }

    /**
     * Call a method with 6 arguments on the remote target.
     * @param target the remote target(s) that should invoke the method.
     * @param method the method to call, should be a static method. No lambdas or instance methods.
     */
    public static <T1, T2, T3, T4, T5, T6>
    void call(
            RPCTarget target,
            Fun6<T1, T2, T3, T4, T5, T6> method,
            T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6
    ) {
        RpcSupport.call(target, method, arg1, arg2, arg3, arg4, arg5, arg6);
    }

    /**
     * Call a method with 7 arguments on the remote target.
     * @param target the remote target(s) that should invoke the method.
     * @param method the method to call, should be a static method. No lambdas or instance methods.
     */
    public static <T1, T2, T3, T4, T5, T6, T7>
    void call(
            RPCTarget target,
            Fun7<T1, T2, T3, T4, T5, T6, T7> method,
            T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7
    ) {
        RpcSupport.call(target, method, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }

    /**
     * Call a method with 8 arguments on the remote target.
     * @param target the remote target(s) that should invoke the method.
     * @param method the method to call, should be a static method. No lambdas or instance methods.
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8>
    void call(
            RPCTarget target,
            Fun8<T1, T2, T3, T4, T5, T6, T7, T8> method,
            T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7, T8 arg8
    ) {
        RpcSupport.call(target, method, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
    }
}
