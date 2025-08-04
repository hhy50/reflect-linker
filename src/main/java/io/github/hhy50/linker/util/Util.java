package io.github.hhy50.linker.util;

import io.github.hhy50.linker.runtime.Runtime;

import java.lang.reflect.InvocationTargetException;
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
    public static Class<?> expandIndexType(List<Object> index, Class<?> type) {
        if (index == null) return type;
        if (index.size() > TypeUtil.getArrayDimension(type)) {
            return Object.class;
        }
        return type.getComponentType();
    }
}
