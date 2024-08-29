package io.github.hhy.linker.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectUtil {
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
}