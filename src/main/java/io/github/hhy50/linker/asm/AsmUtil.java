package io.github.hhy50.linker.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * The type Asm util.
 */
public class AsmUtil {

    /**
     * Areturn.
     *
     * @param mv    the mv
     * @param rType the r type
     */
    public static void areturn(MethodVisitor mv, Type rType) {
        if (rType.getSort() == Type.VOID) {
            mv.visitInsn(Opcodes.RETURN);
        } else {
            mv.visitInsn(rType.getOpcode(Opcodes.IRETURN));
        }
    }

    /**
     * Adapt ldc class type.
     *
     * @param visitor the visitor
     * @param type    the type
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
     * Add args desc type.
     *
     * @param methodType the method type
     * @param newArg     the new arg
     * @param header     the header
     * @return the type
     */
    public static Type addArgsDesc(Type methodType, Type newArg, boolean header) {
        String delimiter = header ? "\\(" : "\\)";
        String[] split = methodType.getDescriptor().split(delimiter);
        split[0] += newArg.getDescriptor();
        return Type.getMethodType(header ? ("("+split[0]+split[1]) : (split[0]+")"+split[1]));
    }

    /**
     * Areturn null.
     *
     * @param mv    the mv
     * @param rType the r type
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
     * Calculate lvb offset int.
     *
     * @param isStatic      the is static
     * @param argumentTypes the argument types
     * @return the int
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
     * Throw no such method.
     *
     * @param write      the write
     * @param methodName the method name
     */
    public static void throwNoSuchMethod(MethodVisitor write, String methodName) {
        write.visitTypeInsn(Opcodes.NEW, "java/lang/NoSuchMethodError");
        write.visitInsn(Opcodes.DUP);
        write.visitLdcInsn(methodName);
        write.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/NoSuchMethodError", "<init>", "(Ljava/lang/String;)V", false);
        write.visitInsn(Opcodes.ATHROW);
    }
}
