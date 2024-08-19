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

    /**
     * 验证是否有父子关系
     * @param child
     * @param parent
     * @return
     */
    public static boolean isAssignableFrom(Class<?> child, Class<?> parent) {
        Class<?> superclass = child.getSuperclass();
        while (superclass != null && superclass != Object.class) {
            if (superclass == parent) {
                return true;
            }
            superclass = superclass.getSuperclass();
        }
        return false;
    }
}
