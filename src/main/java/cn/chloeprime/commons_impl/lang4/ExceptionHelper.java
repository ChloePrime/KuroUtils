package cn.chloeprime.commons_impl.lang4;

public final class ExceptionHelper {
    /**
     * Copied from NoException: <a href="https://noexception.machinezoo.com">https://noexception.machinezoo.com</a>
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> RuntimeException sneak(Throwable exception) throws T {
        throw (T) exception;
    }

    private ExceptionHelper() {
    }
}
