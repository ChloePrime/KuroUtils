package cn.chloeprime.commons_impl;

import cn.chloeprime.commons_impl.rpc.serialization.RpcSerializers;
import cn.chloeprime.commons_impl.network.KUNetwork;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(KuroUtilsMod.MODID)
public class KuroUtilsMod {
    public static final String MODID = "kuroutils";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }

    public KuroUtilsMod(FMLJavaModLoadingContext context) {
        var bus = context.getModEventBus();
        bus.register(this);
        RpcSerializers.init(bus);
    }

    @SubscribeEvent
    public final void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(KUNetwork::init);
    }
}
