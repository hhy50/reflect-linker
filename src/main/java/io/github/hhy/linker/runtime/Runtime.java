package io.github.hhy.linker.runtime;

import lombok.SneakyThrows;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;


public class Runtime {

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

//
//    @SneakyThrows
//    public static MethodHandle findSetter(String className, String fieldName) {
//        for (Constructor<?> constructor : MethodHandles.Lookup.class.getDeclaredConstructors()) {
//            if (constructor.getParameterCount() == 2) {
//                constructor.setAccessible(true);
//                return (MethodHandles.Lookup) constructor.newInstance(callerClass,
//                        MethodHandles.Lookup.PUBLIC | MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED | MethodHandles.Lookup.PACKAGE);
//            }
//        }
//        return null;
//    }
//
//    @SneakyThrows
//    public static MethodHandle findSetter(String className, String fieldName) {
//        for (Constructor<?> constructor : MethodHandles.Lookup.class.getDeclaredConstructors()) {
//            if (constructor.getParameterCount() == 2) {
//                constructor.setAccessible(true);
//                return (MethodHandles.Lookup) constructor.newInstance(callerClass,
//                        MethodHandles.Lookup.PUBLIC | MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED | MethodHandles.Lookup.PACKAGE);
//            }
//        }
//        return null;
//    }
}
