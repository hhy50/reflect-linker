package io.github.hhy50.linker.util;

import io.github.hhy50.linker.runtime.Runtime;

import java.lang.reflect.*;
import java.util.*;

/**
 * The type Util.
 */
public class Util {
    /**
     * Gets or else default.
     *
     * @param value       the value
     * @param defaultVale the default vale
     * @return the or else default
     */
    public static String getOrElseDefault(String value, String defaultVale) {
        if (value == null) return defaultVale;
        if (value.equals("")) return defaultVale;
        return value;
    }

    /**
     * New collection collection.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return the collection
     * @throws InstantiationException    the instantiation exception
     * @throws IllegalAccessException    the illegal access exception
     * @throws NoSuchMethodException     the no such method exception
     * @throws InvocationTargetException the invocation target exception
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> newCollection(Class<? extends Collection> clazz) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (clazz.getName().startsWith("java.util.")) {
            return clazz.newInstance();
        }
        if (Set.class.isAssignableFrom(clazz)) {
            return new HashSet<>();
        } else if (List.class.isAssignableFrom(clazz)) {
            return new ArrayList<>();
        }
        return clazz.getConstructor().newInstance();
    }

    /**
     * Gets class.
     *
     * @param classLoader the class loader
     * @param item        the item
     * @return the class
     */
    public static Class getClass(ClassLoader classLoader, String item) {
        if (item.equals("void")) return void.class;

        try {
            return Runtime.getClass(classLoader, item);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Expand index type class.
     *
     * @param index the index
     * @param type  the type
     * @return the class
     */
    public static Class<?> expandArrayIndexType(List<Object> index, Class<?> type) {
        if (index == null || index.isEmpty()) return type;
        if (index.size() > TypeUtil.getArrayDimension(type)) {
            return Object.class;
        }
        Class<?> currentType = type;
        for (int i = 0; i < index.size(); i++) {
            currentType = currentType.getComponentType();
        }
        return currentType;
    }

    /**
     *
     */
    public static Class<?> expandFieldIndexType(List<Object> index, Field field) {
        Class<?> currentType = field.getType();
        if (index == null || index.isEmpty()) return currentType;

        if (currentType.isArray()) {
            return expandArrayIndexType(index, currentType);
        }
        Type currentGenericType = field.getGenericType();
        for (int i = 0; i < index.size(); i++) {
            if (currentType != null && currentType.isArray()) {
                currentType = currentType.getComponentType();
                currentGenericType = currentType;
            } else if (currentType != null && List.class.isAssignableFrom(currentType)) {
                Type itemType = getListItemType(currentGenericType);
                currentGenericType = itemType;
                currentType = expandIndex(itemType);
            } else {
                return Object.class;
            }
        }
        return currentType;
    }

    public static Type getListItemType(Type genericType) {
        if (genericType instanceof ParameterizedType) {
            Type[] typeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
            if (typeArguments.length > 0) return typeArguments[typeArguments.length - 1];
        }
        return Object.class;
    }

    public static Class<?> expandIndex(Type type) {
        if (type instanceof Class) return (Class<?>) type;
        if (type instanceof ParameterizedType) return (Class<?>) ((ParameterizedType) type).getRawType();
        if (type instanceof GenericArrayType) {
            Class<?> componentType = expandIndex(((GenericArrayType) type).getGenericComponentType());
            return Array.newInstance(componentType, 0).getClass();
        }
        return Object.class; // TypeVariable/WildcardType等无法解析
    }
}
