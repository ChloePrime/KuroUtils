package cn.chloeprime.commons_impl.rpc.client;

import cn.chloeprime.commons_impl.rpc.MethodKnowledgeDatabase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientMethodIdKnowledgeCleanup {
    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        MethodKnowledgeDatabase.onClientLogout();
    }
}
