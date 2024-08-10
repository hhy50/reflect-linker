package io.github.hhy.linker.bytecode.vars;


import io.github.hhy.linker.bytecode.MethodBody;
import org.objectweb.asm.Opcodes;

public class MethodHandleMember extends Member {

    private final String invokeDesc;

    /**
     * @param owner
     * @param mhVarName
     */
    public MethodHandleMember(String owner, String mhVarName, String invokeDesc) {
        super(owner, mhVarName, MethodHandleVar.DESCRIPTOR);
        this.invokeDesc = invokeDesc;
    }


    public ObjectVar invoke(MethodBody methodBody, ObjectVar... args) {
        int resLvb = methodBody.lvbIndex++;
        methodBody.append(write -> {
            write.visitVarInsn(Opcodes.ALOAD, 0);
            write.visitFieldInsn(Opcodes.GETFIELD, owner, memberName, type);
            for (ObjectVar arg : args) {
                write.visitVarInsn(Opcodes.ALOAD, arg.lvbIndex);
            }
            write.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invoke", invokeDesc, false);
        });
        return new ObjectVar(resLvb);
    }
}
