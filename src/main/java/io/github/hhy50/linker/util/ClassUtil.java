package io.github.hhy50.linker.util;


import io.github.hhy50.linker.annotations.Typed;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>ClassUtil class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class ClassUtil {
    /**
     * <p>className2path.</p>
     *
     * @param clazz a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String className2path(String clazz) {
        return clazz.replace('.', '/');
    }

    /**
     * <p>classpath2name.</p>
     *
     * @param clazz a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String classpath2name(String clazz) {
        return clazz.replace('/', '.');
    }

    /**
     * <p>toSimpleName.</p>
     *
     * @param className a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String toSimpleName(String className) {
        return className.substring(className.lastIndexOf(".")+1);
    }

    /**
     * <p>isAssignableFrom.</p>
     *
     * @param child a {@link java.lang.Class} object.
     * @param parent a {@link java.lang.Class} object.
     * @return a boolean.
     */
    public static boolean isAssignableFrom(Class<?> child, Class<?> parent) {
        return isAssignableFrom(child, parent.getName());
    }

    /**
     * <p>isAssignableFrom.</p>
     *
     * @param child a {@link java.lang.Class} object.
     * @param parent a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static boolean isAssignableFrom(Class<?> child, String parent) {
        if (child.getName().equals(parent) || parent.equals("java.lang.Object")) return true;
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
     * <p>getTypeDefines.</p>
     *
     * @param define a {@link java.lang.Object} object.
     * @param <T> a T object.
     * @return a {@link java.util.Map} object.
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

    /**
     * <p>polymorphismMatch.</p>
     *
     * @param parameters an array of {@link java.lang.reflect.Parameter} objects.
     * @param argTypes an array of {@link java.lang.String} objects.
     * @return a boolean.
     */
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
