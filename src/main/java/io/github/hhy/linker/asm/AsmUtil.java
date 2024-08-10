package io.github.hhy.linker.asm;

import io.github.hhy.linker.bytecode.InvokeClassImplBuilder;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class AsmUtil {

    public static String toTypeDesc(String className) {
        return "L" + ClassUtil.className2path(className) + ";";
    }

    public static AsmClassBuilder defineClass(int access, String className, String superName, String[] interfaces, String sign) {
        return new AsmClassBuilder(access, className, superName, interfaces, sign);
    }

    public static InvokeClassImplBuilder defineImplClass(int access, String className, String superName, String[] interfaces, String sign) {
        return new InvokeClassImplBuilder(access, className, superName, interfaces, sign);
    }

    public static void loadArgs(MethodVisitor bytecode, boolean isStatic, Type[] argumentTypes) {
        int i = isStatic ? 0 : 1;
        for (Type argumentType : argumentTypes) {
            bytecode.visitVarInsn(argumentType.getOpcode(Opcodes.ILOAD), i);
            if (argumentType.getSort() == Type.DOUBLE || argumentType.getSort() == Type.LONG) i++;
            i++;
        }
    }

    public static void areturn(MethodVisitor writer, Type rType) {
        writer.visitTypeInsn(Opcodes.CHECKCAST, rType.getDescriptor());
        if (rType.getSort() == Type.VOID) {
            writer.visitInsn(Opcodes.RETURN);
        } else {
            writer.visitInsn(rType.getOpcode(Opcodes.IRETURN));
        }
    }

    public static void areturnNull(MethodVisitor writer, Type rType) {
        writer.visitInsn(Opcodes.ACONST_NULL);
        if (rType.getSort() == Type.VOID) {
            writer.visitInsn(Opcodes.RETURN);
        } else {
            writer.visitInsn(rType.getOpcode(Opcodes.IRETURN));
        }
    }

    /**
     * 计算本地变量表长度
     *
     * @param argumentTypes
     * @return
     */
    public static int calculateLvbOffset(boolean isStatic, Type[] argumentTypes) {
        int lvbLen = isStatic ? 0 : 1; // this
        for (Type argumentType : argumentTypes) {
            int setup = argumentType.getSort() == Type.DOUBLE || argumentType.getSort() == Type.LONG ? 2 : 1;
            lvbLen += setup;
        }
        return lvbLen;
    }

    public static void throwNoSuchMethod(MethodVisitor write, String methodName) {
        write.visitTypeInsn(Opcodes.NEW, "java/lang/NoSuchMethodError");
        write.visitInsn(Opcodes.DUP);
        write.visitLdcInsn(methodName);
        write.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/NoSuchMethodError", "<init>", "(Ljava/lang/String;)V", false);
        write.visitInsn(Opcodes.ATHROW);
    }

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
}
