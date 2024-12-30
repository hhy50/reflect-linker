package io.github.hhy50.linker.runtime;

import io.github.hhy50.linker.AccessTool;
import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.exceptions.LinkerException;
import io.github.hhy50.linker.syslinker.LookupLinker;
import io.github.hhy50.linker.util.ClassUtil;
import io.github.hhy50.linker.util.ReflectUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.reflect.*;
import java.util.Arrays;

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
    public static final MethodDescriptor FIND_FIELD = MethodDescriptor.of(Runtime.OWNER, "findField", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Class;");
    /**
     * The constant FIND_GETTER.
     */
    public static final MethodDescriptor FIND_GETTER = MethodDescriptor.of(Runtime.OWNER, "findGetter", Runtime.FIND_GETTER_DESC);
    /**
     * The constant FIND_SETTER.
     */
    public static final MethodDescriptor FIND_SETTER = MethodDescriptor.of(Runtime.OWNER, "findSetter", Runtime.FIND_SETTER_DESC);
    /**
     * The constant FIND_METHOD.
     */
    public static final MethodDescriptor FIND_METHOD = MethodDescriptor.of(Runtime.OWNER, "findMethod", Runtime.FIND_METHOD_DESC);
    /**
     * The constant LOOKUP.
     */
    public static final MethodDescriptor LOOKUP = MethodDescriptor.of(Runtime.OWNER, "lookup", "(Ljava/lang/Class;)Ljava/lang/invoke/MethodHandles$Lookup;");
    /**
     * The constant GET_CLASS.
     */
    public static final MethodDescriptor GET_CLASS = MethodDescriptor.of(Runtime.OWNER, "getClass", "(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;");

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
     * @param cl        the cl
     * @param className the caller class name
     * @return the class
     * @throws ClassNotFoundException the class not found exception
     */
    public static Class<?> getClass(ClassLoader cl, String className) throws ClassNotFoundException {
        Class<?> clazz = ClassUtil.getPrimitiveClass(className);
        if (clazz != null) return clazz;
        if (className.endsWith("[]")) {
            return Array.newInstance(cl.loadClass(className.substring(0, className.length()-2)), 0).getClass();
        }
        return cl.loadClass(className);
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
            if (argsType.length == 1 && argsType[0].equals("java.lang.Object[]")) {
                return new InvokeDynamic(lookup, superClass, clazz, methodName)
                        .dynamicInvoker();
            }
            throw new NoSuchMethodException("not found method '"+methodName+"' in class "+clazz.getName());
        }
        return superClass == null ? lookup.unreflect(method) : lookup.unreflectSpecial(method, clazz);
    }

    static class InvokeDynamic extends MutableCallSite {
        static final MethodHandle BOOTSTRAP_METHOD;

        static {
            try {
                BOOTSTRAP_METHOD = MethodHandles.lookup().findVirtual(InvokeDynamic.class,
                        "bootstrap", MethodType.methodType(Object.class, Object.class, Object[].class));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        private final String superClass;
        private final Class<?> clazz;
        private final String methodName;
        private final MethodHandles.Lookup lookup;

        public InvokeDynamic(MethodHandles.Lookup lookup, String superClass, Class<?> clazz, String methodName) {
            super(MethodType.methodType(Object.class, Object.class, Object[].class));
            this.superClass = superClass;
            this.clazz = clazz;
            this.methodName = methodName;
            this.lookup = lookup;
            setTarget(BOOTSTRAP_METHOD.bindTo(this));
        }

        public Object bootstrap(Object obj, Object[] args) throws Throwable {
            Method method = ReflectUtil.matchMethod(clazz, methodName, superClass,
                    Arrays.stream(args).map(Object::getClass).map(Class::getName).toArray(String[]::new));
            if (method == null) {
                throw new NoSuchMethodException("not found method '"+methodName+"' in class "+clazz.getName());
            }
            MethodHandle mh = superClass == null ? lookup.unreflect(method) : lookup.unreflectSpecial(method, clazz);
            if (!Modifier.isStatic(method.getModifiers())) {
                mh = mh.bindTo(obj);
            }
            return mh.invokeWithArguments(args);
        }
    }
}
