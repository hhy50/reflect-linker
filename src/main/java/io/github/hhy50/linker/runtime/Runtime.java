package io.github.hhy50.linker.runtime;

import io.github.hhy50.linker.AccessTool;
import io.github.hhy50.linker.entity.MethodHolder;
import io.github.hhy50.linker.exceptions.LinkerException;
import io.github.hhy50.linker.syslinker.LookupLinker;
import io.github.hhy50.linker.util.ReflectUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;


/**
 * <p>Runtime class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class Runtime {
    /** Constant <code>OWNER="io/github/hhy50/linker/runtime/Runtime"</code> */
    public static String OWNER = "io/github/hhy50/linker/runtime/Runtime";
    /** Constant <code>FIND_GETTER_DESC="(Ljava/lang/invoke/MethodHandles$Lookup"{trunked}</code> */
    public static String FIND_GETTER_DESC = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;)Ljava/lang/invoke/MethodHandle;";
    /** Constant <code>FIND_SETTER_DESC="(Ljava/lang/invoke/MethodHandles$Lookup"{trunked}</code> */
    public static String FIND_SETTER_DESC = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;)Ljava/lang/invoke/MethodHandle;";
    /** Constant <code>FIND_METHOD_DESC="(Ljava/lang/invoke/MethodHandles$Lookup"{trunked}</code> */
    public static String FIND_METHOD_DESC = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)Ljava/lang/invoke/MethodHandle;";
    /** Constant <code>FIND_GETTER</code> */
    public static final MethodHolder FIND_GETTER = new MethodHolder(Runtime.OWNER, "findGetter", Runtime.FIND_GETTER_DESC);
    /** Constant <code>FIND_SETTER</code> */
    public static final MethodHolder FIND_SETTER = new MethodHolder(Runtime.OWNER, "findSetter", Runtime.FIND_SETTER_DESC);
    /** Constant <code>FIND_METHOD</code> */
    public static final MethodHolder FIND_METHOD = new MethodHolder(Runtime.OWNER, "findMethod", Runtime.FIND_METHOD_DESC);
    /** Constant <code>FIND_LOOKUP</code> */
    public static final MethodHolder FIND_LOOKUP = new MethodHolder(Runtime.OWNER, "findLookup", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/invoke/MethodHandles$Lookup;");
    /** Constant <code>LOOKUP</code> */
    public static final MethodHolder LOOKUP = new MethodHolder(Runtime.OWNER, "lookup", "(Ljava/lang/Class;)Ljava/lang/invoke/MethodHandles$Lookup;");
    /** Constant <code>GET_CLASS</code> */
    public static final MethodHolder GET_CLASS = new MethodHolder(Runtime.OWNER, "getClass", "(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;");

    /** Constant <code>mhl</code> */
    public static final LookupLinker LOOKUP_LINKER;

    static {
        try {
            LOOKUP_LINKER = AccessTool.createSysLinker(LookupLinker.class, null);
        } catch (LinkerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>lookup.</p>
     *
     * @param callerClass a {@link java.lang.Class} object.
     * @return a {@link java.lang.invoke.MethodHandles.Lookup} object.
     * @throws java.lang.reflect.InvocationTargetException if any.
     * @throws java.lang.InstantiationException if any.
     * @throws java.lang.IllegalAccessException if any.
     */
    public static MethodHandles.Lookup lookup(Class<?> callerClass) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        for (Constructor<?> constructor : MethodHandles.Lookup.class.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 2) {
                constructor.setAccessible(true);
                return (MethodHandles.Lookup) constructor.newInstance(callerClass,
                        MethodHandles.Lookup.PUBLIC | MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED | MethodHandles.Lookup.PACKAGE);
            }
        }
//        return LOOKUP_LINKER.lookupImpl();
        return null;
    }

    /**
     * <p>getClass.</p>
     *
     * @param cl a {@link java.lang.ClassLoader} object.
     * @param callerClassName a {@link java.lang.String} object.
     * @return a {@link java.lang.Class} object.
     * @throws java.lang.ClassNotFoundException if any.
     */
    public static Class<?> getClass(ClassLoader cl, String callerClassName) throws ClassNotFoundException {
        if (callerClassName.endsWith("[]")) {
            return Array.newInstance(cl.loadClass(callerClassName.substring(0, callerClassName.length()-2)), 0).getClass();
        }
        return cl.loadClass(callerClassName);
    }

    /**
     * <p>findLookup.</p>
     *
     * @param clazz a {@link java.lang.Class} object.
     * @param fieldName a {@link java.lang.String} object.
     * @return a {@link java.lang.invoke.MethodHandles.Lookup} object.
     * @throws java.lang.reflect.InvocationTargetException if any.
     * @throws java.lang.InstantiationException if any.
     * @throws java.lang.IllegalAccessException if any.
     */
    public static MethodHandles.Lookup findLookup(Class<?> clazz, String fieldName) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Field field = ReflectUtil.getField(clazz, fieldName);
        return lookup(field.getType());
    }

    /**
     * 获取Getter
     *
     * @param lookup a {@link java.lang.invoke.MethodHandles.Lookup} object.
     * @param fieldName a {@link java.lang.String} object.
     * @return a {@link java.lang.invoke.MethodHandle} object.
     * @throws java.lang.IllegalAccessException if any.
     * @throws java.lang.reflect.InvocationTargetException if any.
     * @throws java.lang.InstantiationException if any.
     * @throws java.lang.NoSuchFieldException if any.
     */
    public static MethodHandle findGetter(MethodHandles.Lookup lookup, String fieldName) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        return findGetter(lookup, lookup.lookupClass(), fieldName);
    }

    /**
     * <p>findGetter.</p>
     *
     * @param lookup a {@link java.lang.invoke.MethodHandles.Lookup} object.
     * @param clazz a {@link java.lang.Class} object.
     * @param fieldName a {@link java.lang.String} object.
     * @return a {@link java.lang.invoke.MethodHandle} object.
     * @throws java.lang.IllegalAccessException if any.
     * @throws java.lang.reflect.InvocationTargetException if any.
     * @throws java.lang.InstantiationException if any.
     * @throws java.lang.NoSuchFieldException if any.
     */
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
     * @param lookup a {@link java.lang.invoke.MethodHandles.Lookup} object.
     * @param fieldName a {@link java.lang.String} object.
     * @return a {@link java.lang.invoke.MethodHandle} object.
     * @throws java.lang.IllegalAccessException if any.
     * @throws java.lang.reflect.InvocationTargetException if any.
     * @throws java.lang.InstantiationException if any.
     * @throws java.lang.NoSuchFieldException if any.
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
     * @param methodName a {@link java.lang.String} object.
     * @param lookup a {@link java.lang.invoke.MethodHandles.Lookup} object.
     * @param superClass a {@link java.lang.String} object.
     * @param argsType an array of {@link java.lang.String} objects.
     * @return a {@link java.lang.invoke.MethodHandle} object.
     * @throws java.lang.IllegalAccessException if any.
     * @throws java.lang.NoSuchMethodException if any.
     * @throws java.lang.reflect.InvocationTargetException if any.
     * @throws java.lang.InstantiationException if any.
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
