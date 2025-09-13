package cn.chloeprime.commons;

import cn.chloeprime.commons_impl.CommonProxy;
import cn.chloeprime.commons_impl.rpc.Endpoint;
import net.minecraft.core.RegistryAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ContextUtil {
    /**
     * Get {@link RegistryAccess}, crashes if failed
     *
     * @return the {@link RegistryAccess}, always nonnull
     */
    @NotNull
    public static RegistryAccess getRegistryAccess() {
        return CommonProxy
                .getRegistryAccess()
                .orElseThrow(() -> new IllegalStateException("Accessing RegistryAccess too early!"));
    }

    /**
     * Get {@link RegistryAccess}, returns null if failed
     *
     * @return the {@link RegistryAccess}, returns null if failed
     */
    @Nullable
    public static RegistryAccess getRegistryAccessSafely() {
        return CommonProxy
                .getRegistryAccess()
                .orElse(null);
    }

    /**
     * Get the local {@link Endpoint} for this machine.
     *
     * @return {@link Endpoint#SERVER} if called on the server, elsewise returns the endpoint for the current client.
     */
    public static Endpoint getLocalEndpoint() {
        return CommonProxy.getLocalEndpoint();
    }
}
