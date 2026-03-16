package cn.chloeprime.commons.math;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3i;

/**
 * Casting of various linear algebra types from different frameworks.
 *
 * @since 2.3.0
 * @author ChloePrime
 */
@SuppressWarnings("unused")
public class LinearAlgebraTypes {
    /**
     * Cast {@link Vec3i} to {@link Vec3},
     * usually used to cast normals of {@link net.minecraft.core.Direction}.
     *
     * @param normal an integer vector.
     * @return a double vector with the same value as {@code normal}.
     */
    public static Vec3 cast(Vec3i normal) {
        return new Vec3(normal.getX(), normal.getY(), normal.getZ());
    }

    /**
     * Cast Minecraft's {@link Vec2} to JOML's {@link Vector2f}
     *
     * @param mojVec 2d float vector from Minecraft.
     * @return 2d float vector from JOML.
     */
    public static Vector2f moj2joml(Vec2 mojVec) {
        return new Vector2f(mojVec.x, mojVec.y);
    }

    /**
     * Cast Minecraft's {@link Vec3} to JOML's {@link Vector3d}
     *
     * @param mojVec 3d vector from Minecraft.
     * @return 3d vector from JOML.
     */
    public static Vector3d moj2joml(Vec3 mojVec) {
        return new Vector3d(mojVec.x(), mojVec.y(), mojVec.z());
    }

    /**
     * Cast Minecraft's {@link Vec3i} and {@link BlockPos} to JOML's {@link Vector3i}
     *
     * @param mojVec 3d integer vector from Minecraft.
     * @return 3d integer vector from JOML.
     */
    public static Vector3i moj2joml(Vec3i mojVec) {
        return new Vector3i(mojVec.getX(), mojVec.getY(), mojVec.getZ());
    }

    /**
     * Cast JOML's {@link Vector2f} to Minecraft's {@link Vec2}
     *
     * @param vec 2d float vector from JOML.
     * @return 2d float vector from Minecraft.
     */
    public static Vec2 joml2moj(Vector2f vec) {
        return new Vec2(vec.x(), vec.y());
    }

    /**
     * Cast JOML's {@link Vector3d} to Minecraft's {@link Vec3}
     *
     * @param vec 3d vector from JOML.
     * @return 3d vector from Minecraft.
     */
    public static Vec3 joml2moj(Vector3d vec) {
        return new Vec3(vec.x(), vec.y(), vec.z());
    }

    /**
     * Cast JOML's {@link Vector3i} to Minecraft's {@link BlockPos} (which is a subclass of {@link Vec3i})
     *
     * @param vec 3d integer vector from JOML.
     * @return 3d integer vector from Minecraft.
     */
    public static BlockPos joml2moj(Vector3i vec) {
        return new BlockPos(vec.x(), vec.y(), vec.z());
    }
}
