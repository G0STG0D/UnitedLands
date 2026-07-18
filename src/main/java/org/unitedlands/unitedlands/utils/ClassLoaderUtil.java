package org.unitedlands.unitedlands.utils;

public final class ClassLoaderUtil {

    private ClassLoaderUtil() {
    }

    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get();
    }

    public static <T> T withPluginClassLoader(Class<?> pluginClass,
            ThrowingSupplier<T> action) {
        ClassLoader original = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(pluginClass.getClassLoader());
        try {
            return action.get();
        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }
}