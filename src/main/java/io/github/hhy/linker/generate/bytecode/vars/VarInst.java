package io.github.hhy.linker.generate.bytecode.vars;


import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.action.Action;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * VarInstance
 * 生成可以复用的字节码
 */

public abstract class VarInst {

    /**
     * 当前变量在局部变量表中的索引
     */
    private int lvbIndex;

    /**
     * 类型
     */
    protected Type type;

    public VarInst(int lvbIndex, Type type) {
        this.lvbIndex = lvbIndex;
        this.type = type;
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
        methodBody.append(mv -> {
            // 基本数据没法校验
            if (type.getSort() > Type.DOUBLE) {
                Label nlabel = new Label();
                mv.visitVarInsn(type.getOpcode(Opcodes.ILOAD), lvbIndex);
                mv.visitJumpInsn(Opcodes.IFNONNULL, nlabel);
                mv.visitTypeInsn(Opcodes.NEW, "java/lang/NullPointerException");
                mv.visitInsn(Opcodes.DUP);
                mv.visitLdcInsn(nullerr);
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/NullPointerException", "<init>", "(Ljava/lang/String;)V", false);
                mv.visitInsn(Opcodes.ATHROW);
                mv.visitLabel(nlabel);
            }
        });
    }

    /**
     * lpad到栈上
     *
     * @return
     */
    public void load(MethodBody methodBody) {
        methodBody.append(mv -> {
            mv.visitVarInsn(type.getOpcode(Opcodes.ILOAD), lvbIndex);
        });
    }

    /**
     * store到局部变量表
     *
     * @return
     */
    public void store(MethodBody methodBody) {
        methodBody.append(mv -> {
            mv.visitVarInsn(type.getOpcode(Opcodes.ISTORE), lvbIndex);
        });
    }

    public void store(MethodBody body, Action action) {
        MethodVisitor mv = body.getWriter();
        action.apply(body);
        mv.visitVarInsn(type.getOpcode(Opcodes.ISTORE), lvbIndex);
    }

    public void getClass(MethodBody methodBody) {
        methodBody.append(mv -> {
            mv.visitVarInsn(ALOAD, lvbIndex); // obj
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
        });
    }

    public Type getType() {
        return type;
    }
}
