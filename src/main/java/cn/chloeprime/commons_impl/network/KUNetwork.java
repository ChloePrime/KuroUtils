package cn.chloeprime.commons_impl.network;

import cn.chloeprime.commons_impl.rpc.packet.RpcMethodAcknowledgmentPacket;
import cn.chloeprime.commons_impl.rpc.packet.RpcCallMethodPacket;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class KUNetwork {
    public static void init() {
    }

    @SubscribeEvent
    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(String.valueOf(1));

        registrar.playBidirectional(
                RpcCallMethodPacket.TYPE,
                StreamCodec.of(((buf, value) -> value.encode(buf)), RpcCallMethodPacket::decode),
                RpcCallMethodPacket::handle);

        registrar.executesOn(HandlerThread.NETWORK).playBidirectional(
                RpcMethodAcknowledgmentPacket.TYPE,
                RpcMethodAcknowledgmentPacket.STREAM_CODEC,
                RpcMethodAcknowledgmentPacket::handle
        );
    }

    private KUNetwork() {
    }
}
