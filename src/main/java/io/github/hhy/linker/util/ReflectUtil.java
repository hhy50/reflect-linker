package io.github.hhy.linker.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtil {

    public static Field[] getDeclaredFields(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        return clazz.getDeclaredFields();
    }

    public static Field getDeclaredField(Class<?> clazz, String fieldName) {
        if (clazz == null) {
            return null;
        }
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) return field;
        }
        if (clazz.getSuperclass() != Object.class) {
            return getDeclaredField(clazz.getSuperclass(), fieldName);
        }
        return null;
    }

    public static Method getDeclaredMethod(Class<?> clazz, String methodName) {
        if (clazz == null) {
            return null;
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) return method;
        }
        if (clazz.getSuperclass() != Object.class) {
            return getDeclaredMethod(clazz.getSuperclass(), methodName);
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
        return null;
    }


    public static Method[] getDeclaredMethods(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        return clazz.getDeclaredMethods();
    }

    public static <T> T createInstance(Class<T> clazz) {
        T t = null;
        try {
            t = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return t;
    }
}