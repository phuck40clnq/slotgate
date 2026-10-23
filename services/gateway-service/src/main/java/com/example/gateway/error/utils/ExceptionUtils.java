package com.example.gateway.error.utils;

public final class ExceptionUtils {
    
    private ExceptionUtils() {}

    public static boolean hasCause(
        Throwable ex,
        Class<? extends Throwable> type
    ) {
        Throwable cause = ex;
        while (cause != null) {
            if (type.isInstance(cause)) {
                return true;
            }
            cause = cause.getCause();
        }

        return false;
    }

    public static <T extends Throwable> T findCause(
        Throwable ex,
        Class<T> type
    ) {
        Throwable cause = ex;
        while (cause != null) {
            if (type.isInstance(cause)) {
                return type.cast(cause);
            }
            cause = cause.getCause();
        }
        return null;
    }

}
