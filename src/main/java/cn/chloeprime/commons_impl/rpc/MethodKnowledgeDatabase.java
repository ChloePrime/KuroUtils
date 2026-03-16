package cn.chloeprime.commons_impl.rpc;

import cn.chloeprime.commons_impl.rpc.packet.RpcClearClientKnowledgePacket;
import cn.chloeprime.commons_impl.rpc.packet.RpcMethodAcknowledgmentPacket;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracks MethodIDs that should be known by certain remote.
 */
@EventBusSubscriber
public record MethodKnowledgeDatabase(
        MutableInt idCounter,
        BiMap<LambdaReflectResult, MethodID> knownMethods
) {
    public MethodKnowledgeDatabase() {
        this(new MutableInt(0), HashBiMap.create());
    }

    /**
     * Used when invoking RPC methods.
     */
    public static final Map<Endpoint, MethodKnowledgeDatabase> LOCAL = new HashMap<>();

    /**
     * Used when sending RPC packets.
     */
    public static final Map<Endpoint, MethodKnowledgeDatabase> REMOTE = new HashMap<>();

    public static MethodID ensureRemoteKnowledge(@NotNull Endpoint endpoint, LambdaReflectResult lambda) {
        MethodID methodID;

        synchronized (REMOTE) {
            var remoteKnowledge = REMOTE.get(endpoint);
            if (remoteKnowledge == null) {
                // Remote joined the database first time,
                // Always sends acknowledgement packet.
                methodID = MethodID.ZERO;
                remoteKnowledge = new MethodKnowledgeDatabase();
                remoteKnowledge.knownMethods().put(lambda, methodID);
                remoteKnowledge.idCounter().setValue(1);
                REMOTE.put(endpoint, remoteKnowledge);
            } else {
                // Remote already known to the database,
                // Check whether the method is known.
                MethodID remoteKnownID = remoteKnowledge.knownMethods().get(lambda);
                if (remoteKnownID != null) {
                    return remoteKnownID;
                } else {
                    methodID = MethodID.of(remoteKnowledge.idCounter().getAndIncrement());
                    remoteKnowledge.knownMethods().put(lambda, methodID);
                }
            }
        }

        endpoint.send(new RpcMethodAcknowledgmentPacket(methodID.value(), lambda));
        return methodID;
    }

    @Nullable
    public static LambdaReflectResult getLocalKnowledge(Endpoint endpoint, MethodID id) {
        synchronized (LOCAL) {
            var db = LOCAL.get(endpoint);
            if (db == null) {
                return null;
            }
            return db.knownMethods().inverse().get(id);
        }
    }

    public static void putLocalKnowledge(IPayloadContext context, RpcMethodAcknowledgmentPacket packet) {
        var endpoint = context.flow().getReceptionSide().isServer()
                ? Endpoint.forPlayer((ServerPlayer) context.player())
                : Endpoint.SERVER;

        if (LOCAL_BAD_METHOD_IDS.getInt(endpoint) == packet.id()) {
            return;
        }

        @Nullable LambdaReflectResult lambda;
        try {
            lambda = RpcSupport.recreateKnowledgeFromPacket(packet.clazz(), packet.methodName(), packet.methodSignature()).orElse(null);
        } catch (ReflectiveOperationException ex) {
            RpcSupport.LOGGER.error("Failed to recreate MethodHandle from packet", ex);
            lambda = null;
        }
        if (lambda == null) {
            LOCAL_BAD_METHOD_IDS.put(endpoint, packet.id());
        } else {
            putLocalKnowledge(endpoint, MethodID.of(packet.id()), lambda);
        }
    }

    public static void putLocalKnowledge(Endpoint endpoint, MethodID id, LambdaReflectResult lambda) {
        synchronized (LOCAL) {
            var database = LOCAL.computeIfAbsent(endpoint, _endpoint -> new MethodKnowledgeDatabase());
            database.knownMethods().put(lambda, id);
        }
    }

    private static final Object2IntMap<Endpoint> LOCAL_BAD_METHOD_IDS = new Object2IntLinkedOpenHashMap<>();

    static {
        LOCAL_BAD_METHOD_IDS.defaultReturnValue(-1);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @ApiStatus.Internal
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        // 登录时再清空一次知识库，
        // 这样就可以支持群组服了。
        // （在客户端看来切换群组服只是换了世界而没有退服，所以需要从服务端重置知识库）
        if (event.getEntity() instanceof ServerPlayer player) {
            clearKnowledgeFor(Endpoint.forPlayer(player));
            PacketDistributor.sendToPlayer(player, new RpcClearClientKnowledgePacket());
        }
    }

    @SubscribeEvent
    @ApiStatus.Internal
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            clearKnowledgeFor(Endpoint.forPlayer(player));
        }
    }

    @ApiStatus.Internal
    public static void clearForClient() {
        clearKnowledgeFor(Endpoint.SERVER);
    }

    private static void clearKnowledgeFor(Endpoint endpoint) {
        synchronized (LOCAL) {
            synchronized (REMOTE) {
                LOCAL.remove(endpoint);
                REMOTE.remove(endpoint);
            }
        }
    }
}
