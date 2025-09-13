package cn.chloeprime.commons_impl;

import cn.chloeprime.commons_impl.rpc.serialization.RpcSerializers;
import cn.chloeprime.commons_impl.network.KUNetwork;
import cn.chloeprime.commons_impl.xver.CrossVersionHelper;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(KuroUtilsMod.MODID)
public class KuroUtilsMod {
    public static final String MODID = "kuroutils";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation loc(String path) {
        return CrossVersionHelper.identifier(MODID, path);
    }

    public KuroUtilsMod(IEventBus bus) {
        bus.register(this);
        RpcSerializers.init(bus);
    }

    @SubscribeEvent
    public final void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(KUNetwork::init);
    }
}
