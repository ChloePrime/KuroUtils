package cn.chloeprime.commons_impl.lang4;

import cn.chloeprime.commons.lang4.Formula;
import cn.chloeprime.commons_impl.KuroUtilsMod;
import org.openjdk.nashorn.api.scripting.ClassFilter;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.annotation.Nullable;
import javax.script.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class FormulaSupport {
    private static final @Nullable NashornScriptEngineFactory SCRIPT_ENGINE_FACTORY = new NashornScriptEngineFactory();
    private static final ClassFilter DISABLE_JAVA_ACCESS = clazz -> false;

    public static Formula ofNashornJavascript(String code, double fallback) {
        if (SCRIPT_ENGINE_FACTORY == null) {
            throw new UnsupportedOperationException("No Javascript engine installed");
        }
        var engine = SCRIPT_ENGINE_FACTORY.getScriptEngine(DISABLE_JAVA_ACCESS);
        CompiledScript compiled;
        try {
            compiled = ((Compilable) engine).compile(code);
        } catch (ScriptException ex) {
            KuroUtilsMod.LOGGER.error("Failed to compile formula {}", code, ex);
            return bindings -> fallback;
        }
        var bad = new AtomicBoolean();
        return bindings -> {
            try {
                return bad.get() ? fallback : ((Number) compiled.eval(convertToBindings(bindings))).doubleValue();
            } catch (ScriptException ex) {
                bad.set(true);
                KuroUtilsMod.LOGGER.error("Failed to evaluate formula {}", code, ex);
                return fallback;
            }
        };
    }

    private static Bindings convertToBindings(Map<String, Object> args) {
        if (args instanceof Bindings bindings) {
            return bindings;
        } else {
            return new SimpleBindings(new HashMap<>(args));
        }
    }
}
