package io.github.hhy.linker.runtime;

import io.github.hhy.linker.AccessTool;
import io.github.hhy.linker.entity.MethodHolder;
import io.github.hhy.linker.exceptions.LinkerException;
import io.github.hhy.linker.syslinker.LookupLinker;
import io.github.hhy.linker.syslinker.MethodHandleLinker;
import io.github.hhy.linker.util.ReflectUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;


public class Runtime {
    public static String OWNER = "io/github/hhy/linker/runtime/Runtime";
    public static String FIND_GETTER_DESC = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;)Ljava/lang/invoke/MethodHandle;";
    public static String FIND_SETTER_DESC = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;)Ljava/lang/invoke/MethodHandle;";
    public static String FIND_METHOD_DESC = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)Ljava/lang/invoke/MethodHandle;";
    public static final MethodHolder FIND_GETTER = new MethodHolder(Runtime.OWNER, "findGetter", Runtime.FIND_GETTER_DESC);
    public static final MethodHolder FIND_SETTER = new MethodHolder(Runtime.OWNER, "findSetter", Runtime.FIND_SETTER_DESC);
    public static final MethodHolder FIND_METHOD = new MethodHolder(Runtime.OWNER, "findMethod", Runtime.FIND_METHOD_DESC);
    public static final MethodHolder FIND_LOOKUP = new MethodHolder(Runtime.OWNER, "findLookup", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/invoke/MethodHandles$Lookup;");
    public static final MethodHolder LOOKUP = new MethodHolder(Runtime.OWNER, "lookup", "(Ljava/lang/Class;)Ljava/lang/invoke/MethodHandles$Lookup;");
    public static final MethodHolder GET_CLASS = new MethodHolder(Runtime.OWNER, "getClass", "(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;");

    private static final LookupLinker LOOKUP_LINKER;

    static {
        try {
            LOOKUP_LINKER = AccessTool.createSysLinker(LookupLinker.class, null);
            LOOKUP_LINKER.lookupImpl(); // init
        } catch (LinkerException e) {
            throw new RuntimeException(e);
        }
    }

    public static MethodHandles.Lookup lookup(Class<?> callerClass) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        for (Constructor<?> constructor : MethodHandles.Lookup.class.getDeclaredConstructors()) {
//            if (constructor.getParameterCount() == 2) {
//                constructor.setAccessible(true);
//                return (MethodHandles.Lookup) constructor.newInstance(callerClass,
//                        MethodHandles.Lookup.PUBLIC | MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED | MethodHandles.Lookup.PACKAGE);
//            } else if (constructor.getParameterCount() == 3) {
                MethodHandleLinker mhLinker = null;
                try {
                    mhLinker = AccessTool.createSysLinker(MethodHandleLinker.class, null);
                } catch (LinkerException e) {
                    throw new RuntimeException(e);
                }
                mhLinker.privateLookupIn(callerClass, null);
//            }
        }
        return null;
    }

    public static Class<?> getClass(ClassLoader cl, String callerClassName) throws ClassNotFoundException {
        if (callerClassName.endsWith("[]")) {
            return Array.newInstance(cl.loadClass(callerClassName.substring(0, callerClassName.length()-2)), 0).getClass();
        }
        return cl.loadClass(callerClassName);
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
     * @param methodName
     * @return
     */
    public static MethodHandle findMethod(MethodHandles.Lookup lookup, String methodName, String superClass, String[] argsType) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        Method method = ReflectUtil.matchMethod(lookup.lookupClass(), methodName, superClass, argsType);
        if (method == null) {
            throw new NoSuchMethodException("not found method '"+methodName+"' in class "+lookup.lookupClass().getName());
        }
        if (lookup.lookupClass() != method.getDeclaringClass() && !method.isAccessible()) {
            lookup = lookup(method.getDeclaringClass());
        }
        return superClass == null ? lookup.unreflect(method) : lookup.unreflectSpecial(method, lookup.lookupClass());
    }
}
