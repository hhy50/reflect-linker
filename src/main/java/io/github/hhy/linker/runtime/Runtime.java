package io.github.hhy.linker.runtime;

import io.github.hhy.linker.util.ReflectUtil;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;


public class Runtime {

    public static MethodHandles.Lookup lookup(Class callerClass) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        for (Constructor<?> constructor : MethodHandles.Lookup.class.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 2) {
                constructor.setAccessible(true);
                return (MethodHandles.Lookup) constructor.newInstance(callerClass,
                        MethodHandles.Lookup.PUBLIC | MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED | MethodHandles.Lookup.PACKAGE);
            }
        }
        return null;
    }

    /**
     * 获取字段类型
     * @param obj
     * @param fieldName
     * @return
     */
    public static Class<?> getFieldType(Object obj, String fieldName) {
        Field field = ReflectUtil.getField(obj.getClass(), fieldName);
        return field.getType();
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
