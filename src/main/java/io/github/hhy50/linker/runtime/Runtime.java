package io.github.hhy50.linker.runtime;

import io.github.hhy50.linker.AccessTool;
import io.github.hhy50.linker.entity.MethodHolder;
import io.github.hhy50.linker.exceptions.LinkerException;
import io.github.hhy50.linker.syslinker.LookupLinker;
import io.github.hhy50.linker.util.ReflectUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * The type Runtime.
 */
public class Runtime {
    /**
     * The constant OWNER.
     */
    public static String OWNER = "io/github/hhy50/linker/runtime/Runtime";
    /**
     * The constant FIND_GETTER_DESC.
     */
    public static String FIND_GETTER_DESC = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/invoke/MethodHandle;";
    /**
     * The constant FIND_SETTER_DESC.
     */
    public static String FIND_SETTER_DESC = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/invoke/MethodHandle;";
    /**
     * The constant FIND_METHOD_DESC.
     */
    public static String FIND_METHOD_DESC = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)Ljava/lang/invoke/MethodHandle;";
    /**
     * The constant FIND_FIELD.
     */
    public static final MethodHolder FIND_FIELD = new MethodHolder(Runtime.OWNER, "findField", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Class;");
    /**
     * The constant FIND_GETTER.
     */
    public static final MethodHolder FIND_GETTER = new MethodHolder(Runtime.OWNER, "findGetter", Runtime.FIND_GETTER_DESC);
    /**
     * The constant FIND_SETTER.
     */
    public static final MethodHolder FIND_SETTER = new MethodHolder(Runtime.OWNER, "findSetter", Runtime.FIND_SETTER_DESC);
    /**
     * The constant FIND_METHOD.
     */
    public static final MethodHolder FIND_METHOD = new MethodHolder(Runtime.OWNER, "findMethod", Runtime.FIND_METHOD_DESC);
    /**
     * The constant LOOKUP.
     */
    public static final MethodHolder LOOKUP = new MethodHolder(Runtime.OWNER, "lookup", "(Ljava/lang/Class;)Ljava/lang/invoke/MethodHandles$Lookup;");
    /**
     * The constant GET_CLASS.
     */
    public static final MethodHolder GET_CLASS = new MethodHolder(Runtime.OWNER, "getClass", "(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;");

    /**
     * The constant LOOKUP_LINKER.
     */
    public static final LookupLinker LOOKUP_LINKER;

    static {
        try {
            LOOKUP_LINKER = AccessTool.createSysLinker(LookupLinker.class, null);
        } catch (LinkerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lookup method handles . lookup.
     *
     * @param callerClass the caller class
     * @return the method handles . lookup
     * @throws IllegalAccessException the illegal access exception
     */
    public static MethodHandles.Lookup lookup(Class<?> callerClass) throws IllegalAccessException {
        if (callerClass == MethodHandles.Lookup.class) {
            return RuntimeUtil.getLookupByUnsafe();
        }
        return LOOKUP_LINKER.lookupImpl();
    }

    /**
     * Gets class.
     *
     * @param cl              the cl
     * @param callerClassName the caller class name
     * @return the class
     * @throws ClassNotFoundException the class not found exception
     */
    public static Class<?> getClass(ClassLoader cl, String callerClassName) throws ClassNotFoundException {
        if (callerClassName.endsWith("[]")) {
            return Array.newInstance(cl.loadClass(callerClassName.substring(0, callerClassName.length()-2)), 0).getClass();
        }
        return cl.loadClass(callerClassName);
    }

    /**
     * Find field class.
     *
     * @param clazz     the clazz
     * @param fieldName the field name
     * @return the class
     * @throws NoSuchFieldException the no such field exception
     */
    public static Class<?> findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field field = ReflectUtil.getField(clazz, fieldName);
        if (field == null) {
            throw new NoSuchFieldException("not found property '"+fieldName+"' in class '"+clazz.getName()+"'");
        }
        return field.getType();
    }

    /**
     * Find getter method handle.
     *
     * @param lookup    the lookup
     * @param clazz     the clazz
     * @param fieldName the field name
     * @return the method handle
     * @throws IllegalAccessException    the illegal access exception
     * @throws InvocationTargetException the invocation target exception
     * @throws InstantiationException    the instantiation exception
     * @throws NoSuchFieldException      the no such field exception
     */
    public static MethodHandle findGetter(MethodHandles.Lookup lookup, Class<?> clazz, String fieldName) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        Field field = ReflectUtil.getField(clazz, fieldName);
        if (field == null) {
            throw new NoSuchFieldException("not found property '"+fieldName+"' in class '"+clazz.getName()+"'");
        }
        return lookup.unreflectGetter(field);
    }


    /**
     * Find setter method handle.
     *
     * @param lookup    the lookup
     * @param clazz     the clazz
     * @param fieldName the field name
     * @return the method handle
     * @throws IllegalAccessException    the illegal access exception
     * @throws InvocationTargetException the invocation target exception
     * @throws InstantiationException    the instantiation exception
     * @throws NoSuchFieldException      the no such field exception
     */
    public static MethodHandle findSetter(MethodHandles.Lookup lookup, Class<?> clazz, String fieldName) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        Field field = ReflectUtil.getField(clazz, fieldName);
        if (field == null) {
            throw new NoSuchFieldException("not found property '"+fieldName+"' in class "+clazz.getName());
        }
        return lookup.unreflectSetter(field);
    }

    /**
     * Find method method handle.
     *
     * @param lookup     the lookup
     * @param clazz      the clazz
     * @param methodName the method name
     * @param superClass the super class
     * @param argsType   the args type
     * @return the method handle
     * @throws IllegalAccessException the illegal access exception
     * @throws NoSuchMethodException  the no such method exception
     */
    public static MethodHandle findMethod(MethodHandles.Lookup lookup, Class<?> clazz, String methodName, String superClass, String[] argsType) throws IllegalAccessException, NoSuchMethodException {
        Method method = ReflectUtil.matchMethod(clazz, methodName, superClass, argsType);
        if (method == null) {
            throw new NoSuchMethodException("not found method '"+methodName+"' in class "+clazz.getName());
        }
        return superClass == null ? lookup.unreflect(method) : lookup.unreflectSpecial(method, clazz);
    }
}
