package cn.chloeprime.commons.lang4;

import cn.chloeprime.commons_impl.lang4.EmptyArraySupport;

/**
 * Empty arrays.
 * Unlike {@code new T[0]}, empty arrays here share the same identity, so they are allocation free.
 */
@SuppressWarnings("unused")
public final class EmptyArrays {
    /**
     * An empty boolean array ({@code boolean[0]})
     */
    public static final boolean[]   BOOLEAN = new boolean[0];

    /**
     * An empty byte array ({@code byte[0]})
     */
    public static final byte[]      BYTE    = new byte[0];

    /**
     * An empty char array ({@code char[0]})
     */
    public static final char[]      CHAR    = new char[0];

    /**
     * An empty short array ({@code short[0]})
     */
    public static final short[]     SHORT   = new short[0];

    /**
     * An empty int array ({@code int[0]})
     */
    public static final int[]       INT     = new int[0];

    /**
     * An empty long array ({@code long[0]})
     */
    public static final long[]      LONG    = new long[0];

    /**
     * An empty float array ({@code float[0]})
     */
    public static final float[]     FLOAT   = new float[0];

    /**
     * An empty double array ({@code double[0]})
     */
    public static final double[]    DOUBLE  = new double[0];

    /**
     * An empty {@link String} array ({@code String[0]})
     */
    public static final String[]    STRING  = ofType(String.class);

    /**
     * An empty object array ({@code Object[0]})
     */
    public static final Object[]    OBJECT  = ofType(Object.class);

    /**
     * Obtain the shared empty array with the given element type.
     *
     * @param type The element type of the empty array.
     * @return The empty array with the given element type.
     * @param <T> The declared element type of the empty array.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] ofType(Class<T> type) {
        return (T[]) EmptyArraySupport.EMPTY_ARRAY_MAP.get(type);
    }

    private EmptyArrays() {
    }
}
