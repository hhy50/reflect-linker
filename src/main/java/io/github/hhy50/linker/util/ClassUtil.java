package io.github.hhy50.linker.util;


import io.github.hhy50.linker.annotations.Typed;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Class util.
 */
public class ClassUtil {
    /**
     * Class name 2 path string.
     *
     * @param clazz the clazz
     * @return the string
     */
    public static String className2path(String clazz) {
        return clazz.replace('.', '/');
    }

    /**
     * Classpath 2 name string.
     *
     * @param clazz the clazz
     * @return the string
     */
    public static String classpath2name(String clazz) {
        return clazz.replace('/', '.');
    }

    /**
     * To simple name string.
     *
     * @param className the class name
     * @return the string
     */
    public static String toSimpleName(String className) {
        return className.substring(className.lastIndexOf(".")+1);
    }

    /**
     * Is assignable from boolean.
     *
     * @param child  the child
     * @param parent the parent
     * @return the boolean
     */
    public static boolean isAssignableFrom(Class<?> child, Class<?> parent) {
        return isAssignableFrom(child, parent.getName());
    }

    /**
     * Is assignable from boolean.
     *
     * @param child  the child
     * @param parent the parent
     * @return the boolean
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
     * Gets type defines.
     *
     * @param <T>    the type parameter
     * @param define the define
     * @return the type defines
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
     * Polymorphism match boolean.
     *
     * @param parameters the parameters
     * @param argTypes   the arg types
     * @return the boolean
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

    /**
     * Gets primitive class.
     *
     * @param clazz the clazz
     * @return primitive class
     */
    public static Class<?> getPrimitiveClass(String clazz) {
        if (clazz.equals("byte[]")) {
            return byte[].class;
        }
        if (clazz.equals("short[]")) {
            return short[].class;
        }
        if (clazz.equals("int[]")) {
            return int[].class;
        }
        if (clazz.equals("long[]")) {
            return long[].class;
        }
        if (clazz.equals("char[]")) {
            return char[].class;
        }
        if (clazz.equals("boolean[]")) {
            return boolean[].class;
        }
        if (clazz.equals("double[]")) {
            return double[].class;
        }
        if (clazz.equals("float[]")) {
            return float[].class;
        }
        if (clazz.equals("byte")) {
            return byte.class;
        }
        if (clazz.equals("short")) {
            return short.class;
        }
        if (clazz.equals("int")) {
            return int.class;
        }
        if (clazz.equals("long")) {
            return long.class;
        }
        if (clazz.equals("float")) {
            return float.class;
        }
        if (clazz.equals("double")) {
            return double.class;
        }
        if (clazz.equals("char")) {
            return char.class;
        }
        if (clazz.equals("boolean")) {
            return boolean.class;
        }
        return null;
    }
}
