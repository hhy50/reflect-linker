package io.github.hhy.linker.runtime;

import io.github.hhy.linker.util.ReflectUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


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
     * 获取Getter
     * @param obj
     * @param fieldName
     * @return
     */
    public static MethodHandle findGetter(MethodHandles.Lookup lookup, Object obj, String fieldName) throws IllegalAccessException {
        Field field = ReflectUtil.getField(obj.getClass(), fieldName);
        return lookup.unreflectGetter(field);
    }


    /**
     * 获取Setter
     * @param obj
     * @param fieldName
     * @return
     */
    public static MethodHandle findSetter(MethodHandles.Lookup lookup, Object obj, String fieldName) throws IllegalAccessException {
        Field field = ReflectUtil.getField(obj.getClass(), fieldName);
        return lookup.unreflectSetter(field);
    }

    /**
     * 获取 MethodHandle.method
     * @param obj
     * @param methodName
     * @return
     */
    public static MethodHandle findInvokeVirtual(MethodHandles.Lookup lookup, Object obj, String methodName) throws IllegalAccessException {
        Method method = ReflectUtil.getMethod(obj.getClass(), methodName);
        return lookup.unreflect(method);
    }
}
