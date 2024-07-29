package io.github.hhy.linker;

public class LinkerFactory {
    public static  <T> T newInstance(Class<T> define, String targetClass) throws ClassNotFoundException {
        return newInstance(define, Class.forName(targetClass));
    }

    public static <T> T newInstance(Class<T> define, Class<?> targetClass) {
        return null;
    }

    public static <T> T newInstance(Class<T> define, Object target) {
        return null;
    }
}
