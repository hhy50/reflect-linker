package io.github.hhy50.linker.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Reflect util.
 */
public class ReflectUtil {
    /**
     * Gets method.
     *
     * @param clazz      the clazz
     * @param methodName the method name
     * @return the method
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
     * Gets field.
     *
     * @param clazz     the clazz
     * @param fieldName the field name
     * @return the field
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
     * Gets methods.
     *
     * @param clazz      the clazz
     * @param methodName the method name
     * @return the methods
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
     * Match method method.
     *
     * @param clazz      the clazz
     * @param name       the name
     * @param superClass the super class
     * @param argTypes   the arg types
     * @return the method
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

    public static void setFieldValue(Object obj, String fieldName, Object val) throws IllegalAccessException {
        Field field = getField(obj.getClass(), fieldName);
        field.setAccessible(true);
        field.set(obj, val);
    }
}
