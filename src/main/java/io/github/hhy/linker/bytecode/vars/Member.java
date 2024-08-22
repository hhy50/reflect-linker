package io.github.hhy.linker.bytecode.vars;

import io.github.hhy.linker.bytecode.MethodBody;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public abstract class Member {

    private int access = ACC_PUBLIC;

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

    public void load(MethodBody methodBody) {
        methodBody.append(mv -> {
            if ((access & Opcodes.ACC_STATIC) > 0) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, this.owner, this.memberName, this.type.getDescriptor());
            } else {
                mv.visitVarInsn(Opcodes.ALOAD, 0); // this
                mv.visitFieldInsn(Opcodes.GETFIELD, this.owner, this.memberName, this.type.getDescriptor());
            }
        });
    }

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

    public void getClassName(MethodBody methodBody) {
        methodBody.append(mv -> {
            load(methodBody);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
        });
    }
}
