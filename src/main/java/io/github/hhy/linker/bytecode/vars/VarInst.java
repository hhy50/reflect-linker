package io.github.hhy.linker.bytecode.vars;


import io.github.hhy.linker.bytecode.MethodBody;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * VarInstance
 * 生成可以复用的字节码
 */

public abstract class VarInst {

    /**
     * 当前变量在局部变量表中的索引
     */
    public int lvbIndex;

    /**
     * 类型
     */
    public String type;

    public VarInst(int lvbIndex, String typeDesc) {
        this.lvbIndex = lvbIndex;
        this.type = typeDesc;
    }

    /**
     * 检查是否为空， 如果变量为空就抛出空指针
     * <pre>
     *     if (var == null) {
     *         throw new NullPointerException(nullerr);
     *     }
     * </pre>
     */
    public void checkNullPointer(MethodBody methodBody, String nullerr) {
        methodBody.append(methodVisitor -> {
            Label nlabel = new Label();
            methodVisitor.visitVarInsn(Type.getType(type).getOpcode(Opcodes.ILOAD), lvbIndex);
            methodVisitor.visitJumpInsn(Opcodes.IFNONNULL, nlabel);
            methodVisitor.visitTypeInsn(Opcodes.NEW, "java/lang/NullPointerException");
            methodVisitor.visitInsn(Opcodes.DUP);
            methodVisitor.visitLdcInsn(nullerr);
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/NullPointerException", "<init>", "(Ljava/lang/String;)V", false);
            methodVisitor.visitInsn(Opcodes.ATHROW);
            methodVisitor.visitLabel(nlabel);
        });
    }

    /**
     * lpad到栈上
     *
     * @return
     */
    public void load(MethodBody methodBody) {
        methodBody.append(writer -> {
            writer.visitVarInsn(Type.getType(type).getOpcode(Opcodes.ILOAD), lvbIndex);
        });
    }

    /**
     * store到局部变量表
     *
     * @return
     */
    public void store(MethodBody methodBody) {
        methodBody.append(writer -> {
            writer.visitVarInsn(Type.getType(type).getOpcode(Opcodes.ISTORE), lvbIndex);
        });
    }
}
