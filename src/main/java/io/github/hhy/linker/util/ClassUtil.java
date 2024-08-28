package io.github.hhy.linker.util;


import io.github.hhy.linker.annotations.Typed;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
     *
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

    /**
     * 从接口类上面获取提前定义好的类型
     *
     * @param define
     * @param <T>
     * @return
     */
    public static <T> Map<String, String> getTypeDefines(Object define) {
        if (define instanceof Class) {
            Typed[] declaredAnnotations = ((Class<?>) define).getDeclaredAnnotationsByType(Typed.class);
            return Arrays.stream(declaredAnnotations).collect(Collectors.toMap(Typed::name, Typed::type));
        } else if (define instanceof Method) {
            Typed[] declaredAnnotations = ((Method) define).getDeclaredAnnotationsByType(Typed.class);
            return Arrays.stream(declaredAnnotations).collect(Collectors.toMap(Typed::name, Typed::type));
        }
        return Collections.emptyMap();
    }

    public static boolean deepEquals(Class<?>[] classes1, Class<?>[] classes2) {
        if (classes1.length != classes2.length) return false;
        for (int i = 0; i < classes1.length; i++) {
            if (!Objects.equals(classes1[i].getName(), classes2[i].getName())) {
                return false;
            }
        }
        return true;
    }

    public static boolean polymorphismMatch(Class<?>[] classes1, Class<?>[] classes2) {
        if (classes1.length != classes2.length) return false;
        for (int i = 0; i < classes1.length; i++) {
            if (isAssignableFrom(classes2[i], classes1[i]) || isAssignableFrom(classes1[i], classes2[i])) {

            } else {
                return false;
            }
        }
        return true;
    }
}
