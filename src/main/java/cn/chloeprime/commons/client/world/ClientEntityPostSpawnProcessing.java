package cn.chloeprime.commons.client.world;

import cn.chloeprime.commons.async.TaskScheduler;
import cn.chloeprime.commons_impl.KuroUtilsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.LogicalSide;
import org.jetbrains.annotations.Contract;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Process entity or wait for their spawn then process.
 * Useful to do extra logic after entity spawning.
 */
@SuppressWarnings("unused")
public final class ClientEntityPostSpawnProcessing {
    public static final int MAX_TRIAGES = 5;

    /**
     * Process an entity. If the entity is not found, wait for its spawn for {@link #MAX_TRIAGES} ticks.
     *
     * @param id the entity id, usually sent from the server.
     * @param logic the process code.
     */
    public static void process(int id, Consumer<Entity> logic) {
        process(id, logic, 1);
    }

    /**
     * Process entities. Wait for their spawn for {@link #MAX_TRIAGES} ticks when encountering.
     *
     * @param ids array of entity ids to process, usually sent from the server.
     * @param logic the process code.
     */
    @Contract(mutates = "param1")
    @SuppressWarnings("UnstableApiUsage")
    public static void process(int[] ids, Consumer<Entity> logic) {
        process(ids, logic, 1);
    }

    private static void process(int id, Consumer<Entity> logic, int tries) {
        var level = Objects.requireNonNull(Minecraft.getInstance()).level;
        if (level != null) {
            var entity = level.getEntity(id);
            if (entity != null) {
                logic.accept(entity);
                return;
            }
        }
        if (tries >= MAX_TRIAGES) {
            return;
        }
        SCHEDULER.delay(1, task -> process(id, logic, tries + 1));
    }

    @Contract(mutates = "param1")
    @SuppressWarnings("UnstableApiUsage")
    private static void process(int[] ids, Consumer<Entity> logic, int tries) {
        var level = Objects.requireNonNull(Minecraft.getInstance()).level;
        int failure = 0;
        if (level == null) {
            failure = -1;
        } else {
            for (int i = 0; i < ids.length; i++) {
                var id = ids[i];
                // 表明这个实体已经在之前的尝试中处理过，直接跳过
                if (id < 0) {
                    continue;
                }
                var entity = level.getEntity(ids[i]);
                if (entity == null) {
                    failure++;
                } else {
                    logic.accept(entity);
                    ids[i] = -1;
                }
            }
        }
        if (failure == 0) {
            return;
        }
        if (tries >= MAX_TRIAGES) {
            if (failure < 0) {
                KuroUtilsMod.LOGGER.warn("[{}] Entity processing failed to find client level after {} triages.",
                        ClientEntityPostSpawnProcessing.class.getSimpleName(),
                        MAX_TRIAGES);
            } else {
                KuroUtilsMod.LOGGER.warn("[{}] Entity processing failed {} times after {} triages.",
                        ClientEntityPostSpawnProcessing.class.getSimpleName(),
                        failure, MAX_TRIAGES);
            }
            return;
        }
        SCHEDULER.delay(1, task -> process(ids, logic, tries + 1));
    }

    private static final TaskScheduler SCHEDULER = TaskScheduler.createTickBased(LogicalSide.CLIENT);

    private ClientEntityPostSpawnProcessing() {
    }
}
