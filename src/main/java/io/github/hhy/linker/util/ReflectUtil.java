package io.github.hhy.linker.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>ReflectUtil class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class ReflectUtil {
    /**
     * <p>getMethod.</p>
     *
     * @param clazz a {@link java.lang.Class} object.
     * @param methodName a {@link java.lang.String} object.
     * @return a {@link java.lang.reflect.Method} object.
     */
    public static Method getMethod(Class<?> clazz, String methodName) {
        if (clazz == null) {
            return null;
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) return method;
        }
        if (clazz.getSuperclass() != Object.class) {
            return getMethod(clazz.getSuperclass(), methodName);
        }
        return null;
    }

    /**
     * <p>getField.</p>
     *
     * @param clazz a {@link java.lang.Class} object.
     * @param fieldName a {@link java.lang.String} object.
     * @return a {@link java.lang.reflect.Field} object.
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        if (clazz == null) {
            return null;
        }
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) return field;
        }
        if (clazz.getSuperclass() != Object.class) {
            return getField(clazz.getSuperclass(), fieldName);
        }
        return null;
    }

    /**
     * <p>getMethods.</p>
     *
     * @param clazz a {@link java.lang.Class} object.
     * @param methodName a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    public static List<Method> getMethods(Class<?> clazz, String methodName) {
        if (clazz == null) {
            return null;
        }

        List<Method> methods = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                methods.add(method);
            }
        }
        if (clazz.getSuperclass() != null) {
            methods.addAll(getMethods(clazz.getSuperclass(), methodName));
        }
        return methods;
    }

    /**
     * <p>matchMethod.</p>
     *
     * @param clazz a {@link java.lang.Class} object.
     * @param name a {@link java.lang.String} object.
     * @param superClass a {@link java.lang.String} object.
     * @param argTypes an array of {@link java.lang.String} objects.
     * @return a {@link java.lang.reflect.Method} object.
     */
    public static Method matchMethod(Class<?> clazz, String name, String superClass, String[] argTypes) {
        // 指定了调用super， 但是没有指定具体哪个super
        if (superClass != null && superClass.equals("")) {
            superClass = null;
            clazz = clazz.getSuperclass();
        }

        while (clazz != null && superClass != null) {
            if (clazz.getName().equals(superClass)) {
                break;
            }
            clazz = clazz.getSuperclass();
        }
        if (clazz == null) {
            return null;
        }

        List<Method> matches = new ArrayList<>();
        for (Method method : ReflectUtil.getMethods(clazz, name)) {
            Parameter[] parameters = method.getParameters();
            if (parameters.length != argTypes.length) continue;
            if (ClassUtil.polymorphismMatch(parameters, argTypes)) {
                matches.add(method);
            }
        }
        if (matches.size() > 0) return matches.get(0);
        return null;
    }
}
