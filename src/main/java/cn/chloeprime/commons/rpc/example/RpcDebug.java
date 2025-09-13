package cn.chloeprime.commons.rpc.example;

import cn.chloeprime.commons.rpc.*;
import cn.chloeprime.commons_impl.KuroUtilsMod;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.client.event.InputEvent;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@EventBusSubscriber(Dist.CLIENT)
public class RpcDebug implements Serializable {
    private static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onInput(InputEvent.Key event) {
        if (!ENABLED) {
            return;
        }
        if (event.getAction() != InputConstants.PRESS) {
            return;
        }
        switch (event.getKey()) {
//            case InputConstants.KEY_C -> new RpcDebug().run();
            case InputConstants.KEY_X -> RPC.call(RPCTarget.toServer(), RpcDebug::staticy, 1, Blocks.BEDROCK);
        }
    }

    @RemoteCallable(flow = RPCFlow.CLIENT_TO_SERVER)
    public static void staticy(int arg0, Block arg2) {
        KuroUtilsMod.LOGGER.info("I'm on {}!!!, {}, {}", EffectiveSide.get(), arg0, arg2);
        RPC.call(RPCTarget.reply(), RpcDebug::reply, arg0, arg2, new EntityType[]{EntityType.CREEPER, EntityType.ENDERMAN}, RecipeType.BLASTING, RPCContext.getSenderPlayer());
    }

    @RemoteCallable(flow = RPCFlow.SERVER_TO_CLIENT)
    public static void reply(int arg0, Block arg2, EntityType<?>[] objArray, RecipeType<?> interfaceBased, @Nullable Player player) {
        KuroUtilsMod.LOGGER.info("I'm {}, I'm on {}!!!, {}, {}, {}, {}, {}", Objects.requireNonNull(player).getName().getString(), EffectiveSide.get(), arg0, arg2, objArray[1], objArray.length, interfaceBased);
        RPC.call(
                RPCTarget.reply(), RpcDebug::reply2,
                new byte[]{0x70, 0x10, 0x20},
                Component.translatable("entity.minecraft.creeper").withStyle(ChatFormatting.GREEN),
                randomNBT(),
                Component.translatable("entity.minecraft.enderman").withStyle(ChatFormatting.BLACK),
                Blocks.ACACIA_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true)
        );
    }

    @RemoteCallable
    private static void reply2(byte[] arg4, Component text, CompoundTag randomNBT, Component text2, BlockState block) {
        KuroUtilsMod.LOGGER.info("[reply2] I'm on {}!!!, {}, {}, {}, {}", EffectiveSide.get(), text, randomNBT.getList("wawa", Tag.TAG_INT).get(1), text2, block);
        RPC.call(RPCTarget.reply(), RpcDebug::crashy);
    }

    @RemoteCallable(flow = RPCFlow.CLIENT_TO_SERVER)
    public static void crashy() {
        // This will log Wrong RPC flow with its stacktrace in the logger
    }

    private static CompoundTag randomNBT() {
        var compound = new CompoundTag();
        compound.putInt("wahaha", 1);
        compound.putLongArray("coca cola le le", new long[]{UUID.randomUUID().getMostSignificantBits(), UUID.randomUUID().getLeastSignificantBits()});
        var list = new ListTag();
        list.add(IntTag.valueOf(1278));
        list.add(IntTag.valueOf(9996));
        compound.put("wawa", list);
        return compound;
    }
}
