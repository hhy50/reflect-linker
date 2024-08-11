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

    public static InvokeClassImplBuilder defineImplClass(int access, String className, String superName, String[] interfaces, String sign) {
        return new InvokeClassImplBuilder(access, className, superName, interfaces, sign);
    }

    public static void areturn(MethodVisitor writer, Type rType) {
        if (rType.getSort() == Type.VOID) {
            writer.visitInsn(Opcodes.RETURN);
        } else {
            writer.visitInsn(rType.getOpcode(Opcodes.IRETURN));
        }
    }

    public static Type addArgsDesc(Type methodType, Type newArg, boolean header) {
        String delimiter = header ? "\\(" : "\\)";
        String[] split = methodType.getDescriptor().split(delimiter);
        split[0] += newArg.getDescriptor();
        return Type.getMethodType(header ? ("(" + split[0] + split[1]) : (split[0] + ")" + split[1]));
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
}
