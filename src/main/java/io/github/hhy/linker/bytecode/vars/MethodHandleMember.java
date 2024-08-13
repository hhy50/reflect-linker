package io.github.hhy.linker.bytecode.vars;


import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.bytecode.MethodBody;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class MethodHandleMember extends Member {

    private final Type methodType;

    /**
     * @param owner
     * @param mhVarName
     */
    public MethodHandleMember(String owner, String mhVarName, Type methodType) {
        super(owner, mhVarName, MethodHandleVar.DESCRIPTOR);
        this.methodType = methodType;
    }


    public ObjectVar invoke(MethodBody methodBody, ObjectVar... args) {
        ObjectVar objectVar = initResultVar(methodBody);
        methodBody.append(write -> {
            load(methodBody); // mh
            for (ObjectVar arg : args) {
                arg.load(methodBody);
            }
            write.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invoke", AsmUtil.addArgsDesc(methodType, Type.getType(Object.class), true).getDescriptor(), false);
            if (objectVar != null) objectVar.store(methodBody);
        });
        return objectVar;
    }

    private ObjectVar initResultVar(MethodBody methodBody) {
        ObjectVar objectVar = null;
        if (methodType.getReturnType().getSort() != Type.VOID) {
            objectVar = new ObjectVar(methodBody.lvbIndex++, methodType.getReturnType().getDescriptor());
        }
        return objectVar;
    }
}
