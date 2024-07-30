package io.github.hhy.linker.util;

import lombok.SneakyThrows;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;

public class Util {
    public static String getOrElseDefault(String value, String defaultVale) {
        if (value == null) return defaultVale;
        if (value.equals("")) return defaultVale;
        return value;
    }

    @SneakyThrows
    public static MethodHandles.Lookup lookup(Class callerClass) {
        for (Constructor<?> constructor : MethodHandles.Lookup.class.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 2) {
                constructor.setAccessible(true);
                return (MethodHandles.Lookup) constructor.newInstance(callerClass,
                        MethodHandles.Lookup.PUBLIC | MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED | MethodHandles.Lookup.PACKAGE);
            }
        }
        return null;
    }
}
