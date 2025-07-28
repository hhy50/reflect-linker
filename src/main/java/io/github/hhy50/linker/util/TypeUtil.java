package io.github.hhy50.linker.util;

import io.github.hhy50.linker.token.ConstToken;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandle;
import java.util.Arrays;
import java.util.List;

/**
 * The type Type utils.
 */
public class TypeUtil {

    /**
     * The constant METHOD_HANDLER_TYPE.
     */
    public static final Type METHOD_HANDLER_TYPE = Type.getType(MethodHandle.class);
    /**
     * The constant STRING_TYPE.
     */
    public static final Type STRING_TYPE = Type.getType(String.class);
    /**
     * The constant CLASS_TYPE.
     */
    public static final Type CLASS_TYPE = Type.getType(Class.class);

    /**
     * Gets method type.
     *
     * @param rType    the r type
     * @param argsType the args type
     * @return the method type
     */
    public static Type getMethodType(Class<?> rType, Class<?>... argsType) {
        return Type.getMethodType(Type.getType(rType), Arrays.stream(argsType)
                .map(Type::getType).toArray(Type[]::new));
    }

    /**
     * Is primitive type boolean.
     *
     * @param type the type
     * @return the boolean
     */
    public static boolean isPrimitiveType(Type type) {
        return type.getSort() > Type.VOID && type.getSort() <= Type.DOUBLE;
    }

    /**
     * Is object type boolean.
     *
     * @param type the type
     * @return the boolean
     */
    public static boolean isObjectType(Type type) {
        return !isPrimitiveType(type);
    }

    /**
     * Is wrap type boolean.
     *
     * @param type the type
     * @return the boolean
     */
    public static boolean isWrapType(Type type) {
        // type 是否是包装类型
        String className = type.getClassName();
        return className.equals("java.lang.Boolean") || className.equals("java.lang.Character") || className.equals("java.lang.Byte")
                || className.equals("java.lang.Short") || className.equals("java.lang.Integer") || className.equals("java.lang.Float")
                || className.equals("java.lang.Long") || className.equals("java.lang.Double");
    }


    /**
     * Gets primitive type.
     *
     * @param type the type
     * @return the primitive type
     */
    public static Type getPrimitiveType(Type type) {
        // 获取对应类型的基本数据类型
        if (type.getClassName().equals("java.lang.Boolean")) {
            return Type.BOOLEAN_TYPE;
        }
        if (type.getClassName().equals("java.lang.Character")) {
            return Type.CHAR_TYPE;
        }
        if (type.getClassName().equals("java.lang.Byte")) {
            return Type.BYTE_TYPE;
        }
        if (type.getClassName().equals("java.lang.Short")) {
            return Type.SHORT_TYPE;
        }
        if (type.getClassName().equals("java.lang.Integer")) {
            return Type.INT_TYPE;
        }
        if (type.getClassName().equals("java.lang.Float")) {
            return Type.FLOAT_TYPE;
        }
        if (type.getClassName().equals("java.lang.Long")) {
            return Type.LONG_TYPE;
        }
        if (type.getClassName().equals("java.lang.Double")) {
            return Type.DOUBLE_TYPE;
        }
        return null;
    }
    public static Type getBoxType(Type type) {
        // 判断是否是基本数据类
        if (type.equals(Type.BOOLEAN_TYPE)) {
            return getType("java.lang.Boolean");
        }
        if (type.equals(Type.BYTE_TYPE)) {
            return getType("java.lang.Byte");
        }
        if (type.equals(Type.SHORT_TYPE)) {
            return getType("java.lang.Short");
        }
        if (type.equals(Type.INT_TYPE)) {
            return getType("java.lang.Integer");
        }
        if (type.equals(Type.LONG_TYPE)) {
            return getType("java.lang.Long");
        }
        if (type.equals(Type.FLOAT_TYPE)) {
            return getType("java.lang.Float");
        }
        if (type.equals(Type.DOUBLE_TYPE)) {
            return getType("java.lang.Double");
        }
        if (type.equals(Type.CHAR_TYPE)) {
            return getType("java.lang.Character");
        }
        return null;
    }

    /**
     * Gets type.
     *
     * @param clazz the clazz
     * @return the type
     */
    public static Type getType(String clazz) {
        String prefix = "";
        while (clazz.endsWith("[]")) {
            clazz = clazz.substring(0, clazz.length()-2);
            prefix += "[";
        }
        Type primitiveType = getPrimitiveType(clazz);
        if (primitiveType != null) {
            return Type.getType(prefix+primitiveType.getDescriptor());
        }
        return Type.getType(prefix+toTypeDesc(clazz));
    }

    /**
     * Gets primitive type.
     *
     * @param clazz the clazz
     * @return the primitive type
     */
    public static Type getPrimitiveType(String clazz) {
        // 判断是否是基本数据类
        if (clazz.equals("byte")) {
            return Type.BYTE_TYPE;
        }
        if (clazz.equals("short")) {
            return Type.SHORT_TYPE;
        }
        if (clazz.equals("int")) {
            return Type.INT_TYPE;
        }
        if (clazz.equals("long")) {
            return Type.LONG_TYPE;
        }
        if (clazz.equals("float")) {
            return Type.FLOAT_TYPE;
        }
        if (clazz.equals("double")) {
            return Type.DOUBLE_TYPE;
        }
        if (clazz.equals("boolean")) {
            return Type.BOOLEAN_TYPE;
        }
        if (clazz.equals("char")) {
            return Type.CHAR_TYPE;
        }
        return null;
    }

    /**
     * To owner string.
     *
     * @param clazzName the clazz name
     * @return the string
     */
    public static String toOwner(String clazzName) {
        if (clazzName == null) return null;
        return clazzName.replace('.', '/');
    }

    /**
     * To type desc string.
     *
     * @param className the class name
     * @return the string
     */
    public static String toTypeDesc(String className) {
        return "L"+ClassUtil.className2path(className)+";";
    }

    public static Class<?> getTypeFromIndex(Class<?> type, List<ConstToken> index) {
        return type;
    }
}
