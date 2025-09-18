package cn.chloeprime.commons.lang4;

import cn.chloeprime.commons_impl.lang4.FormulaSupport;
import cn.chloeprime.commons_impl.lang4.PooledSimpleBindings;

import javax.script.Bindings;
import java.io.Closeable;
import java.util.Map;

/**
 * Represents a formula
 * that accepts multiple inputs and output a number.
 *
 * @author ChloePrime
 * @since 2001.2.1.0, 2101.2.1.0
 */
@FunctionalInterface
public interface Formula {
    /**
     * Get the result of the formula with certain inputs.
     *
     * @param bindings the independent variables.
     * @return the dependent variable (result) of the formula.
     */
    double eval(Map<String, Object> bindings);

    /**
     * Returns a formula represented by the given Javascript code.
     * Requires OpenJDK's Nashorn in the classpath to functional properly.
     *
     * @param code the Javascript source code.
     * @return a formula represented by the given Javascript code.
     */
    static Formula ofJavascript(String code) {
        return ofJavascript(code, 0);
    }

    /**
     * Returns a formula represented by the given Javascript code.
     * Requires OpenJDK's Nashorn in the classpath to functional properly.
     *
     * @param code     the Javascript source code.
     * @param fallback fallback value when compilation or evaluation errors.
     * @return a formula represented by the given Javascript code.
     */
    static Formula ofJavascript(String code, double fallback) {
        try {
            return FormulaSupport.ofNashornJavascript(code, fallback);
        } catch (IncompatibleClassChangeError ex) {
            if (ex.getMessage().contains("ashorn")) {
                throw new UnsupportedOperationException(
                        "You need Nashorn in the classpath to use %s.ofJavascript()".formatted(Formula.class.getSimpleName()),
                        ex);
            } else {
                throw ex;
            }
        }
    }

    /**
     * Returns a temporary, pooled binding map for evaluating.
     * You should use try-with-resource on the returned Binding.
     *
     * @return a temporary, pooled binding map for evaluating.
     */
    static PooledBindings pooledBindings() {
        return PooledSimpleBindings.get();
    }

    /**
     * A temporary, pooled binding map for evaluating.
     * You should use try-with-resource on instances of this interface.
     */
    interface PooledBindings extends Bindings, Closeable {
        /**
         * {@inheritDoc}
         */
        @Override
        void close();
    }
}
