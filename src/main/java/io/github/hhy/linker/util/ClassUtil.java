package io.github.hhy.linker.util;


public class ClassUtil {
    public static String className2path(String clazz) {
        return clazz.replace('.', '/');
    }

    public static String classpath2name(String clazz) {
        return clazz.replace('/', '.');
    }

    public static String toSimpleName(String className) {
        return className.substring(className.lastIndexOf(".") + 1);
    }

    public static boolean isAssignableFrom(Class<?> child, String parent) {
        return true;
    }
}
