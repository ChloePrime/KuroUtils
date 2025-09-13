package cn.chloeprime.commons_impl.rpc;

public record MethodID(int value) {
    public static final MethodID ZERO = new MethodID(0);

    public static MethodID of(int value) {
        return new MethodID(value);
    }
}
