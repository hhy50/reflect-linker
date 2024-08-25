package io.github.hhy.linker.generate.bytecode;

import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.action.Action;
import io.github.hhy.linker.generate.bytecode.action.LoadAction;
import io.github.hhy.linker.generate.bytecode.vars.ObjectVar;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public abstract class Member implements LoadAction {

    private int access;

    /**
     * 所属类
     */
    private String owner;

    /**
     * 成员名称
     */
    private String memberName;

    /**
     * 类型
     */
    private Type type;

    public Member(int access, String owner, String memberName, Type type) {
        this.access = access;
        this.owner = owner;
        this.memberName = memberName;
        this.type = type;
    }

    public String getMemberName() {
        return memberName;
    }

    @Override
    public void load(MethodBody methodBody) {
        MethodVisitor mv = methodBody.getWriter();
        if ((access & Opcodes.ACC_STATIC) > 0) {
            mv.visitFieldInsn(Opcodes.GETSTATIC, this.owner, this.memberName, this.type.getDescriptor());
        } else {
            mv.visitVarInsn(Opcodes.ALOAD, 0); // this
            mv.visitFieldInsn(Opcodes.GETFIELD, this.owner, this.memberName, this.type.getDescriptor());
        }
    }

    @Deprecated
    public void store(MethodBody methodBody) {
        methodBody.append(mv -> {
            if ((access & Opcodes.ACC_STATIC) > 0) {
                mv.visitFieldInsn(Opcodes.PUTSTATIC, this.owner, this.memberName, this.type.getDescriptor());
            } else {
                // 临时变量
                ObjectVar objectVar = new ObjectVar(methodBody.lvbIndex++, this.type);
                objectVar.store(methodBody);

                mv.visitVarInsn(Opcodes.ALOAD, 0);
                objectVar.load(methodBody);
                mv.visitFieldInsn(Opcodes.PUTFIELD, this.owner, this.memberName, this.type.getDescriptor());
            }
        });
    }

    /**
     * @param methodBody
     * @param action
     */
    public void store(MethodBody methodBody, Action action) {
        MethodVisitor mv = methodBody.getWriter();
        if ((access & Opcodes.ACC_STATIC) > 0) {
            action.apply(methodBody);
            mv.visitFieldInsn(Opcodes.PUTSTATIC, this.owner, this.memberName, this.type.getDescriptor());
        } else {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            action.apply(methodBody);
            mv.visitFieldInsn(Opcodes.PUTFIELD, this.owner, this.memberName, this.type.getDescriptor());
        }
    }

    public void getClassName(MethodBody methodBody) {
        methodBody.append(mv -> {
            load(methodBody);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
        });
    }
}
