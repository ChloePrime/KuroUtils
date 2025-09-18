import cn.chloeprime.commons.lang4.Formula;
import cn.chloeprime.commons_impl.lang4.PooledSimpleBindings;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import static org.junit.jupiter.api.Assertions.*;

public class FormulaTest {
    @Test
    public void testFormula() {
        try (var bindings = Formula.pooledBindings()) {
            bindings.put("x", 3);
            assertEquals(11.0, Formula.ofJavascript("x * x + 2").eval(bindings));
            // Test compilation error cases.
            assertEquals(0, Formula.ofJavascript("wtf is this :)").eval(bindings));
            assertEquals(-1, Formula.ofJavascript("wtf is this :)", -1).eval(bindings));
            // Test evaluation error cases.
            assertEquals(-1, Formula.ofJavascript("x + y + z", -1).eval(bindings));
        }
    }

    @Test
    public void testBindingsPooling() {
        ForkJoinPool threads = ForkJoinPool.commonPool();
        List<ForkJoinTask<Void>> tasks = new ArrayList<>(10000);
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10000; i++) {
                tasks.add(threads.submit(() -> {
                    try (var ignored = Formula.pooledBindings()) {
                        return null;
                    }
                }));
            }
        });
        assertDoesNotThrow(() -> {
            for (ForkJoinTask<Void> task : tasks) {
                task.get();
            }
        });
        assertTrue(PooledSimpleBindings.getPooledObjectCount() < 10000);
    }
}
