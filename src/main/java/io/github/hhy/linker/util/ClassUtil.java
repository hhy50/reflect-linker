package io.github.hhy.linker.util;


import io.github.hhy.linker.annotations.Typed;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassUtil {
    public static String className2path(String clazz) {
        return clazz.replace('.', '/');
    }

    public static String classpath2name(String clazz) {
        return clazz.replace('/', '.');
    }

    public static String toSimpleName(String className) {
        return className.substring(className.lastIndexOf(".")+1);
    }

    /**
     * 验证是否有父子关系
     *
     * @param child
     * @param parent
     * @return
     */
    public static boolean isAssignableFrom(Class<?> child, Class<?> parent) {
        return isAssignableFrom(child, parent.getName());
    }

    public static boolean isAssignableFrom(Class<?> child, String parent) {
        if (child.getName().equals(parent)) return true;
        Class<?> superclass = child.getSuperclass();
        while (superclass != null && superclass != Object.class) {
            if (superclass.getName().equals(parent)) {
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

    public static boolean polymorphismMatch(Parameter[] parameters, String[] argTypes) {
        if (parameters.length != argTypes.length) return false;
        for (int i = 0; i < parameters.length; i++) {
            if (!isAssignableFrom(parameters[i].getType(), argTypes[i])) {
                return false;
            }
        }
        return true;
    }
}
