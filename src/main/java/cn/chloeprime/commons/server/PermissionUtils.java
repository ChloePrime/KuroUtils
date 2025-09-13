package cn.chloeprime.commons.server;

import cn.chloeprime.commons_impl.xver.CrossVersionHelper;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContextKey;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

import java.util.Objects;
import java.util.function.Predicate;

public class PermissionUtils {
    /**
     * Create a permission node that is allowed on OP players
     */
    public static PermissionNode<Boolean> createSimple(ResourceLocation name, PermissionDynamicContextKey<?>... dynamics) {
        return new PermissionNode<>(name, PermissionTypes.BOOLEAN, REQUIRES_OP_ONLINE, dynamics);
    }

    /**
     * Create a permission node that is allowed on OP players
     */
    public static PermissionNode<Boolean> createSimple(String namespace, String path, PermissionDynamicContextKey<?>... dynamics) {
        return createSimple(CrossVersionHelper.identifier(namespace, path));
    }

    /**
     * Create a permission node that is allowed on everyone
     */
    public static PermissionNode<Boolean> createSimpleForEveryone(ResourceLocation name, PermissionDynamicContextKey<?>... dynamics) {
        return new PermissionNode<>(name, PermissionTypes.BOOLEAN, EVERYONE, dynamics);
    }

    /**
     * Create a permission node that is allowed on everyone
     */
    public static PermissionNode<Boolean> createSimpleForEveryone(String namespace, String path, PermissionDynamicContextKey<?>... dynamics) {
        return createSimpleForEveryone(CrossVersionHelper.identifier(namespace, path));
    }

    /**
     * Helper for {@link ArgumentBuilder#requires(Predicate)}<p>
     * Usage:
     * <blockquote><pre>{@code
    dispatcher.register(Commands.literal("nuke")
        .requires(PermissionUtils.checker(ModPermissionNodes.NUKE))
        .then(...));
     * }</pre></blockquote>
     */
    public static Predicate<CommandSourceStack> checker(PermissionNode<Boolean> permission) {
        return new Checker(permission)::check;
    }

    public static PermissionNode.PermissionResolver<Boolean> REQUIRES_OP_ONLINE = (player, uid, context) ->
            player != null && player.hasPermissions(player.server.getOperatorUserPermissionLevel());

    public static PermissionNode.PermissionResolver<Boolean> EVERYONE = (player, uid, context) -> true;

    private record Checker(
            PermissionNode<Boolean> node
    ) {
        public boolean check(CommandSourceStack sender) {
            if (sender.isPlayer()) {
                return PermissionAPI.getPermission(Objects.requireNonNull(sender.getPlayer()), node) == Boolean.TRUE;
            } else {
                return true;
            }
        }
    }
}
