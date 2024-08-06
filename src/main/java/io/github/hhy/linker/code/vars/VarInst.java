package io.github.hhy.linker.code.vars;


import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import javax.swing.*;

/**
 * VarInstance
 * 生成可以复用的字节码
 */

public abstract class VarInst {

    /**
     * 当前变量在局部变量表中的索引
     */
    protected final int lvbIndex;

    /**
     * 类型
     */
    protected Type type;

    public VarInst(int lvbIndex, String typeDesc) {
        this.lvbIndex = lvbIndex;
        this.type = Type.getType(typeDesc);
    }

    /**
     * 检查是否为空， 如果变量为空就抛出空指针
     * <pre>
     *     if (var == null) {
     *         throw new NullPointerException(nullerr);
     *     }
     * </pre>
     */
    public void checkNullPointer(MethodVisitor methodVisitor, String nullerr) {
        Label iflabel = new Label();

        methodVisitor.visitVarInsn(load(), lvbIndex);
        methodVisitor.visitJumpInsn(Opcodes.IFNONNULL, iflabel);
        methodVisitor.visitLabel(iflabel);
        methodVisitor.visitTypeInsn(Opcodes.NEW, "java/lang/NullPointerException");
        methodVisitor.visitInsn(Opcodes.DUP);
        methodVisitor.visitLdcInsn(nullerr);
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/NullPointerException", "<init>", "(Ljava/lang/String;)V", false);
        methodVisitor.visitInsn(Opcodes.ATHROW);
    }

    /**
     * 获取load指令
     *
     * @return
     */
    public int load() {
        return type.getOpcode(Opcodes.ILOAD);
    }

    /**
     * 获取store指令
     *
     * @return
     */
    public int store() {
        return type.getOpcode(Opcodes.ISTORE);
    }
}
