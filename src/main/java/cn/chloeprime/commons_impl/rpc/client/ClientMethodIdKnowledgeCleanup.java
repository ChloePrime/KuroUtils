package cn.chloeprime.commons_impl.rpc.client;

import cn.chloeprime.commons_impl.rpc.MethodKnowledgeDatabase;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ClientMethodIdKnowledgeCleanup {
    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        MethodKnowledgeDatabase.onClientLogout();
    }
}
