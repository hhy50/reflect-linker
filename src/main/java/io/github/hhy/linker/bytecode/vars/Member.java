package io.github.hhy.linker.bytecode.vars;

import io.github.hhy.linker.bytecode.MethodBody;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.*;

public abstract class Member {

    public int access = ACC_PUBLIC;

    /**
     * 所属类
     */
    public String owner;

    /**
     * 成员名称
     */
    public String memberName;

    /**
     * 类型
     */
    public String type;

    public Member(int access, String owner, String memberName, String type) {
        this.access = access;
        this.owner = owner;
        this.memberName = memberName;
        this.type = type;
    }

    public Member(String owner, String memberName, String type) {
        this.owner = owner;
        this.memberName = memberName;
        this.type = type;
    }

    public void load(MethodBody methodBody) {
        methodBody.append(mv -> {
            if ((access & Opcodes.ACC_STATIC) > 0) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, this.owner, this.memberName, this.type);
            } else {
                mv.visitVarInsn(Opcodes.ALOAD, 0); // this
                mv.visitFieldInsn(Opcodes.GETFIELD, this.owner, this.memberName, this.type);
            }
        });
    }

    public void store(MethodBody methodBody) {
        methodBody.append(mv -> {
            if ((access & Opcodes.ACC_STATIC) > 0) {
                mv.visitFieldInsn(Opcodes.PUTSTATIC, this.owner, this.memberName, this.type);
            } else {
                // 临时变量
                ObjectVar objectVar = new ObjectVar(methodBody.lvbIndex++, this.type);
                objectVar.store(methodBody);

                mv.visitVarInsn(Opcodes.ALOAD, 0);
                objectVar.load(methodBody);
                mv.visitFieldInsn(Opcodes.PUTFIELD, this.owner, this.memberName, this.type);
            }
        });
    }

    public void getClassName(MethodBody methodBody) {
        methodBody.append(mv -> {
            load(methodBody);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
        });
    }
}
