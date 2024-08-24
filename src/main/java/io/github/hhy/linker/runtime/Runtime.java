package io.github.hhy.linker.runtime;

import io.github.hhy.linker.generate.MethodHolder;
import io.github.hhy.linker.util.ReflectUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class Runtime {
    public static String OWNER = "io/github/hhy/linker/runtime/Runtime";
    public static String LOOKUP_DESC = "(Ljava/lang/Class;)Ljava/lang/invoke/MethodHandles$Lookup;";
    public static String FIND_LOOKUP_DESC = "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/invoke/MethodHandles$Lookup;";
    public static String FIND_GETTER_DESC = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;)Ljava/lang/invoke/MethodHandle;";
    public static String FIND_SETTER_DESC = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;)Ljava/lang/invoke/MethodHandle;";
    public static final MethodHolder FIND_GETTER = new MethodHolder(Runtime.OWNER, "findGetter", Runtime.FIND_GETTER_DESC);

    public static MethodHandles.Lookup lookup(Class<?> callerClass) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        for (Constructor<?> constructor : MethodHandles.Lookup.class.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 2) {
                constructor.setAccessible(true);
                return (MethodHandles.Lookup) constructor.newInstance(callerClass,
                        MethodHandles.Lookup.PUBLIC | MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED | MethodHandles.Lookup.PACKAGE);
            }
        }
        return null;
    }

    public static MethodHandles.Lookup findLookup(Class<?> clazz, String fieldName) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Field field = ReflectUtil.getField(clazz, fieldName);
        return lookup(field.getType());
    }

    /**
     * 获取Getter
     *
     * @param lookup
     * @param fieldName
     * @return
     */
    public static MethodHandle findGetter(MethodHandles.Lookup lookup, String fieldName) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        return findGetter(lookup, lookup.lookupClass(), fieldName);
    }

    public static MethodHandle findGetter(MethodHandles.Lookup lookup, Class<?> clazz, String fieldName) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        Field field = ReflectUtil.getField(clazz, fieldName);
        if (field == null) {
            throw new NoSuchFieldException("not found property '"+fieldName+"' in class '"+lookup.lookupClass().getName()+"'");
        }
        if (lookup.lookupClass() != field.getDeclaringClass() && !field.isAccessible()) {
            lookup = lookup(field.getDeclaringClass());
        }
        return lookup.unreflectGetter(field);
    }


    /**
     * 获取Setter
     *
     * @param lookup
     * @param fieldName
     * @return
     */
    public static MethodHandle findSetter(MethodHandles.Lookup lookup, String fieldName) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        Field field = ReflectUtil.getField(lookup.lookupClass(), fieldName);
        if (field == null) {
            throw new NoSuchFieldException("not found property '"+fieldName+"' in class "+lookup.lookupClass().getName());
        }
        if (lookup.lookupClass() != field.getDeclaringClass() && !field.isAccessible()) {
            lookup = lookup(field.getDeclaringClass());
        }
        return lookup.unreflectSetter(field);
    }

    /**
     * 获取 MethodHandle.method
     *
     * @param obj
     * @param methodName
     * @return
     */
    public static MethodHandle findInvokeVirtual(MethodHandles.Lookup lookup, Object obj, String methodName) throws IllegalAccessException {
        Method method = ReflectUtil.getMethod(obj.getClass(), methodName);
        return lookup.unreflect(method);
    }
}
