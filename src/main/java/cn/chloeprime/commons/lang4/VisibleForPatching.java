package cn.chloeprime.commons.lang4;

import org.spongepowered.asm.mixin.Mixin;

import java.lang.annotation.*;

/**
 * Indicates that a class is public only for patching convenience (e.g. {@link Mixin}).
 * Annotated classes should be considered as a private API.
 *
 * @since 2.5.0
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@SuppressWarnings("unused")
public @interface VisibleForPatching {
}
