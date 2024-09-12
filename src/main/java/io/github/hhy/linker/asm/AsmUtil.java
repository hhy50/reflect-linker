package io.github.hhy.linker.asm;

import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * <p>AsmUtil class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class AsmUtil {

    /**
     * <p>toTypeDesc.</p>
     *
     * @param className a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String toTypeDesc(String className) {
        return "L"+ClassUtil.className2path(className)+";";
    }

    /**
     * <p>defineImplClass.</p>
     *
     * @param access a int.
     * @param className a {@link java.lang.String} object.
     * @param superName a {@link java.lang.String} object.
     * @param interfaces an array of {@link java.lang.String} objects.
     * @param sign a {@link java.lang.String} object.
     * @return a {@link io.github.hhy.linker.generate.InvokeClassImplBuilder} object.
     */
    public static InvokeClassImplBuilder defineImplClass(int access, String className, String superName, String[] interfaces, String sign) {
        return new InvokeClassImplBuilder(access, className, superName, interfaces, sign);
    }

    /**
     * <p>areturn.</p>
     *
     * @param mv a {@link org.objectweb.asm.MethodVisitor} object.
     * @param rType a {@link org.objectweb.asm.Type} object.
     */
    public static void areturn(MethodVisitor mv, Type rType) {
        if (rType.getSort() == Type.VOID) {
            mv.visitInsn(Opcodes.RETURN);
        } else {
            mv.visitInsn(rType.getOpcode(Opcodes.IRETURN));
        }
    }

    /**
     * <p>adaptLdcClassType.</p>
     *
     * @param visitor a {@link org.objectweb.asm.MethodVisitor} object.
     * @param type a {@link org.objectweb.asm.Type} object.
     */
    public static void adaptLdcClassType(MethodVisitor visitor, Type type) {
        if (type.getSort() <= Type.DOUBLE) {
            visitor.visitFieldInsn(Opcodes.GETSTATIC, getPrimitiveClass(type.getClassName()), "TYPE", "Ljava/lang/Class;");
        } else {
            visitor.visitLdcInsn(type);
        }
    }

    private static String getPrimitiveClass(String className) {
        switch (className) {
            case "void":
                return "java/lang/Void";
            case "boolean":
                return "java/lang/Boolean";
            case "char":
                return "java/lang/Character";
            case "byte":
                return "java/lang/Byte";
            case "short":
                return "java/lang/Short";
            case "int":
                return "java/lang/Integer";
            case "float":
                return "java/lang/Float";
            case "long":
                return "java/lang/Long";
            case "double":
                return "java/lang/Double";
            default:
                return "";
        }
    }

    /**
     * <p>addArgsDesc.</p>
     *
     * @param methodType a {@link org.objectweb.asm.Type} object.
     * @param newArg a {@link org.objectweb.asm.Type} object.
     * @param header a boolean.
     * @return a {@link org.objectweb.asm.Type} object.
     */
    public static Type addArgsDesc(Type methodType, Type newArg, boolean header) {
        String delimiter = header ? "\\(" : "\\)";
        String[] split = methodType.getDescriptor().split(delimiter);
        split[0] += newArg.getDescriptor();
        return Type.getMethodType(header ? ("("+split[0]+split[1]) : (split[0]+")"+split[1]));
    }

    /**
     * <p>areturnNull.</p>
     *
     * @param mv a {@link org.objectweb.asm.MethodVisitor} object.
     * @param rType a {@link org.objectweb.asm.Type} object.
     */
    public static void areturnNull(MethodVisitor mv, Type rType) {
        mv.visitInsn(Opcodes.ACONST_NULL);
        if (rType.getSort() == Type.VOID) {
            mv.visitInsn(Opcodes.RETURN);
        } else {
            mv.visitInsn(rType.getOpcode(Opcodes.IRETURN));
        }
    }

    /**
     * 计算本地变量表长度
     *
     * @param argumentTypes an array of {@link org.objectweb.asm.Type} objects.
     * @param isStatic a boolean.
     * @return a int.
     */
    public static int calculateLvbOffset(boolean isStatic, Type[] argumentTypes) {
        int lvbLen = isStatic ? 0 : 1; // this
        for (Type argumentType : argumentTypes) {
            int setup = argumentType.getSort() == Type.DOUBLE || argumentType.getSort() == Type.LONG ? 2 : 1;
            lvbLen += setup;
        }
        return lvbLen;
    }

    /**
     * <p>throwNoSuchMethod.</p>
     *
     * @param write a {@link org.objectweb.asm.MethodVisitor} object.
     * @param methodName a {@link java.lang.String} object.
     */
    public static void throwNoSuchMethod(MethodVisitor write, String methodName) {
        write.visitTypeInsn(Opcodes.NEW, "java/lang/NoSuchMethodError");
        write.visitInsn(Opcodes.DUP);
        write.visitLdcInsn(methodName);
        write.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/NoSuchMethodError", "<init>", "(Ljava/lang/String;)V", false);
        write.visitInsn(Opcodes.ATHROW);
    }

    /**
     *
     * 是否是基本数据类型
     *
     * @param type a {@link org.objectweb.asm.Type} object.
     * @return a boolean.
     */
    public static boolean isPrimitiveType(Type type) {
        return type.getSort() > Type.VOID && type.getSort() <= Type.DOUBLE;
    }

    /**
     * <p>isObjectType.</p>
     *
     * @param type a {@link org.objectweb.asm.Type} object.
     * @return a boolean.
     */
    public static boolean isObjectType(Type type) {
        return !isPrimitiveType(type);
    }

    /**
     * 是否是包装类型
     *
     * @param type a {@link org.objectweb.asm.Type} object.
     * @return a boolean.
     */
    public static boolean isWrapType(Type type) {
        // type 是否是包装类型
        String className = type.getClassName();
        return className.equals("java.lang.Boolean") || className.equals("java.lang.Character") || className.equals("java.lang.Byte")
                || className.equals("java.lang.Short") || className.equals("java.lang.Integer") || className.equals("java.lang.Float")
                || className.equals("java.lang.Long") || className.equals("java.lang.Double");
    }

    /**
     * 获取对应类型的基本数据类型
     *
     * @param type a {@link org.objectweb.asm.Type} object.
     * @return a {@link org.objectweb.asm.Type} object.
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

    /**
     * <p>getType.</p>
     *
     * @param clazz a {@link java.lang.String} object.
     * @return a {@link org.objectweb.asm.Type} object.
     */
    public static Type getType(String clazz) {
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
        return Type.getType(toTypeDesc(clazz));
    }
}
