package io.github.hhy50.linker.util;

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
}
