package cn.chloeprime.commons_impl.rpc.serialization;

import cn.chloeprime.commons_impl.CommonProxy;
import cn.chloeprime.commons_impl.KuroUtilsMod;
import cn.chloeprime.commons_impl.rpc.RpcSupport;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.*;

import static cn.chloeprime.commons_impl.rpc.serialization.RpcParameterSerializer.*;

/**
 * Basically this is just Stream Codec with class information :P
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RpcSerializers {
    /**
     * WARNING: the registry does not contain serializers of registry entries (blocks, items, etc.)
     */
    public static final ResourceKey<Registry<RpcParameterSerializer<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(KuroUtilsMod.loc("rpc_parameter_serializers"));

    // Java
    public static final RpcParameterSerializer<Boolean>     BOOL        = of(boolean.class, ByteBuf::writeBoolean, ByteBuf::readBoolean);
    public static final RpcParameterSerializer<Byte>        BYTE        = of(byte.class, (buf, b) -> buf.writeByte(b), ByteBuf::readByte);
    public static final RpcParameterSerializer<Short>       SHORT       = of(short.class, (buf, s) -> buf.writeShort(s), ByteBuf::readShort);
    public static final RpcParameterSerializer<Character>   CHAR        = of(char.class, (buf, c) -> buf.writeChar(c), ByteBuf::readChar);
    public static final RpcParameterSerializer<Integer>     INT         = of(int.class, FriendlyByteBuf::writeVarInt, FriendlyByteBuf::readVarInt);
    public static final RpcParameterSerializer<Long>        LONG        = of(long.class, FriendlyByteBuf::writeVarLong, FriendlyByteBuf::readVarLong);
    public static final RpcParameterSerializer<Float>       FLOAT       = of(float.class, ByteBuf::writeFloat, ByteBuf::readFloat);
    public static final RpcParameterSerializer<Double>      DOUBLE      = of(double.class, ByteBuf::writeDouble, ByteBuf::readDouble);
    public static final RpcParameterSerializer<String>      STRING      = of(String.class, FriendlyByteBuf::writeUtf, FriendlyByteBuf::readUtf);
    public static final RpcParameterSerializer<byte[]>      BYTES       = ofArray(byte[].class, byte[]::new, arr -> arr.length, ByteBuf::writeBytes, ByteBuf::readBytes);
    public static final RpcParameterSerializer<int[]>       INTS        = of(int[].class, FriendlyByteBuf::writeVarIntArray, FriendlyByteBuf::readVarIntArray);
    public static final RpcParameterSerializer<long[]>      LONGS       = of(long[].class, FriendlyByteBuf::writeLongArray, FriendlyByteBuf::readLongArray);
    public static final RpcParameterSerializer<BigInteger>  BIG_INT     = BYTES.transform(BigInteger.class, BigInteger::new, BigInteger::toByteArray);
    public static final RpcParameterSerializer<BigDecimal>  BIG_DECIMAL = STRING.transform(BigDecimal.class, BigDecimal::new, BigDecimal::toString);
    public static final RpcParameterSerializer<UUID>        UUID        = of(UUID.class, FriendlyByteBuf::writeUUID, FriendlyByteBuf::readUUID);
    public static final RpcParameterSerializer<Date>        DATE        = of(Date.class, FriendlyByteBuf::writeDate, FriendlyByteBuf::readDate);
    public static final RpcParameterSerializer<Instant>     TIMESTAMP   = of(Instant.class, FriendlyByteBuf::writeInstant, FriendlyByteBuf::readInstant);

    // Minecraft
    public static final RpcParameterSerializer<ResourceLocation>    RESOURCE_LOCATION   = of(ResourceLocation.class, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::readResourceLocation);
    @SuppressWarnings("rawtypes")
    public static final RpcParameterSerializer<ResourceKey>         RESOURCE_KEY        = of(ResourceKey.class, RpcSerializers::writeResourceKey, RpcSerializers::readResourceKey);
    @SuppressWarnings("rawtypes")
    public static final RpcParameterSerializer<TagKey>              TAG_KEY             = of(TagKey.class, RpcSerializers::writeTagKey, RpcSerializers::readTagKey);
    public static final RpcParameterSerializer<Vec3>                VECTOR_3            = of(Vec3.class, RpcSerializers::writeVec3, RpcSerializers::readVec3);
    public static final RpcParameterSerializer<Vec3i>               VECTOR_3I           = of(Vec3i.class, RpcSerializers::writeVec3i, RpcSerializers::readVec3i);
    public static final RpcParameterSerializer<BlockPos>            BLOCK_POS           = VECTOR_3I.transform(BlockPos.class, BlockPos::new, bp -> bp);
    public static final RpcParameterSerializer<CompoundTag>         NBT                 = of(CompoundTag.class, CompoundTag.CODEC);
    public static final RpcParameterSerializer<Component>           TEXT                = of(Component.class, ExtraCodecs.COMPONENT);
    public static final RpcParameterSerializer<BlockState>          BLOCK_STATE         = of(BlockState.class, BlockState.CODEC);
    public static final RpcParameterSerializer<ItemStack>           ITEM_STACK          = of(ItemStack.class, ItemStack.CODEC);

    /**
     * WARNING: Nullable
     */
    public static final RpcParameterSerializer<Entity> ENTITY = INT.transform(Entity.class, CommonProxy::getEntityByID, Entity::getId);

    // Registry Entries
    public static final Map<Class<?>, RpcParameterSerializer<?>> BY_TYPE = new LinkedHashMap<>();

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Registry<RpcParameterSerializer<?>> getRegistry0() {
        return Objects.requireNonNull(BuiltInRegistries.REGISTRY.get((ResourceKey) REGISTRY_KEY), "Accessing RpcSerializationManager's registry too early!");
    }

    public static void init(IEventBus bus) {
        var dfr = DeferredRegister.create(REGISTRY_KEY, KuroUtilsMod.MODID);
        dfr.makeRegistry(() -> new RegistryBuilder<RpcParameterSerializer<?>>()
                .hasTags()
                .disableSaving()
                .disableSync());
        // Java
        dfr.register("z", () -> BOOL);
        dfr.register("b", () -> BYTE);
        dfr.register("s", () -> SHORT);
        dfr.register("c", () -> CHAR);
        dfr.register("i", () -> INT);
        dfr.register("j", () -> LONG);
        dfr.register("f", () -> FLOAT);
        dfr.register("d", () -> DOUBLE);
        dfr.register("string", () -> STRING);
        dfr.register("bytes", () -> BYTES);
        dfr.register("ints", () -> INTS);
        dfr.register("longs", () -> LONGS);
        dfr.register("big_integer", () -> BIG_INT);
        dfr.register("big_decimal", () -> BIG_DECIMAL);
        dfr.register("uuid", () -> UUID);
        dfr.register("date", () -> DATE);
        dfr.register("timestamp", () -> TIMESTAMP);
        // Minecraft
        dfr.register("resource_location", () -> RESOURCE_LOCATION);
        dfr.register("resource_key", () -> RESOURCE_KEY);
        dfr.register("tag_key", () -> TAG_KEY);
        dfr.register("vec3", () -> VECTOR_3);
        dfr.register("vec3i", () -> VECTOR_3I);
        dfr.register("block_pos", () -> BLOCK_POS);
        dfr.register("nbt", () -> NBT);
        dfr.register("text", () -> TEXT);
        dfr.register("block_state", () -> BLOCK_STATE);
        dfr.register("item_stack", () -> ITEM_STACK);
        dfr.register("entity", () -> ENTITY);
        // register to bus
        dfr.register(bus);
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(RpcSerializers::bakeSerializers);
    }

    private static void bakeSerializers() {
        bakeSerializersForRegistries();
        bakeSerializersForRegisteredSerializers();
    }

    private static void bakeSerializersForRegisteredSerializers() {
        for (RpcParameterSerializer<?> serializer : getRegistry0()) {
            BY_TYPE.put(serializer.getBaseClass(), serializer);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void bakeSerializersForRegistries() {
        for (Registry registry : BuiltInRegistries.REGISTRY) {
            var type = (Class) RpcSerializationUtils.findCommonParentClasses(registry);
            if (type == null && registry instanceof DefaultedRegistry<?> defaulted) {
                var typeOfDefault = defaulted.get((ResourceLocation) null).getClass();
                type = RpcSerializationUtils.findCommonParentClasses(List.of(typeOfDefault));
            }
            if (type == null) {
                RpcSupport.LOGGER.warn("RPC Serializer for registry {} is null", registry.key().location());
            } else {
                RpcSupport.LOGGER.info("RPC Serializer for registry {} is {}", registry.key().location(), type.getCanonicalName());
            }
            if (type == Object.class) {
                continue;
            }
            BY_TYPE.put(type, RpcParameterSerializer.of(type, (buf, value) -> buf.writeId(registry, value), buf -> buf.readById(registry)));
        }
    }

    private static void writeVec3(FriendlyByteBuf buf, Vec3 vec) {
        buf.writeDouble(vec.x());
        buf.writeDouble(vec.y());
        buf.writeDouble(vec.z());
    }

    private static Vec3 readVec3(FriendlyByteBuf buf) {
        var x = buf.readDouble();
        var y = buf.readDouble();
        var z = buf.readDouble();
        return new Vec3(x, y, z);
    }

    private static void writeVec3i(FriendlyByteBuf buf, Vec3i vec) {
        buf.writeVarInt(vec.getX());
        buf.writeVarInt(vec.getY());
        buf.writeVarInt(vec.getZ());
    }

    private static Vec3i readVec3i(FriendlyByteBuf buf) {
        var x = buf.readVarInt();
        var y = buf.readVarInt();
        var z = buf.readVarInt();
        return new Vec3i(x, y, z);
    }

    private static void writeResourceKey(FriendlyByteBuf buf, ResourceKey<?> key) {
        buf.writeResourceLocation(key.registry());
        buf.writeResourceLocation(key.location());
    }

    private static ResourceKey<?> readResourceKey(FriendlyByteBuf buf) {
        var registry = ResourceKey.createRegistryKey(buf.readResourceLocation());
        return ResourceKey.create(registry, buf.readResourceLocation());
    }

    private static void writeTagKey(FriendlyByteBuf buf, TagKey<?> key) {
        buf.writeResourceLocation(key.registry().location());
        buf.writeResourceLocation(key.location());
    }

    private static TagKey<?> readTagKey(FriendlyByteBuf buf) {
        var registry = ResourceKey.createRegistryKey(buf.readResourceLocation());
        return TagKey.create(registry, buf.readResourceLocation());
    }
}
