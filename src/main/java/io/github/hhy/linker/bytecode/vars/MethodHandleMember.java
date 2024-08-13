package io.github.hhy.linker.bytecode.vars;


import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.bytecode.MethodBody;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class MethodHandleMember extends Member {

    private final Type methodType;

    private Label invokeThisLabel = new Label();
    private Label invokeEndLabel = new Label();

    /**
     * @param owner
     * @param mhVarName
     */
    public MethodHandleMember(String owner, String mhVarName, Type methodType) {
        super(owner, mhVarName, MethodHandleVar.DESCRIPTOR);
        this.methodType = methodType;
    }


    public ObjectVar invoke(MethodBody methodBody, ObjectVar that, ObjectVar... args) {
        ObjectVar result = initResultVar(methodBody);
        methodBody.append(mv -> {
            // if (mh.getClass().getName().contains("DirectMethodHandle$StaticAccessor"))
            getClassName(methodBody);
            mv.visitLdcInsn("DirectMethodHandle$StaticAccessor");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z", false);
            mv.visitJumpInsn(Opcodes.IFEQ, invokeThisLabel);

            // if static
            invokeStatic(result, methodBody, args);
            // else no static
            invokeThis(result, methodBody, that, args);
            mv.visitLabel(invokeEndLabel);
        });
        return result;
    }

    private void invokeStatic(ObjectVar result, MethodBody methodBody, ObjectVar... args) {
        methodBody.append(write -> {
            load(methodBody); // mh
            for (ObjectVar arg : args) {
                arg.load(methodBody);
            }
            write.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invoke", methodType.getDescriptor(), false);
            if (result != null) result.store(methodBody);
            write.visitJumpInsn(Opcodes.GOTO, invokeEndLabel);
        });
    }

    private void invokeThis(ObjectVar result, MethodBody methodBody, ObjectVar that, ObjectVar... args) {
        methodBody.append(mv -> {
            mv.visitLabel(invokeThisLabel);

            load(methodBody); // mh
            that.load(methodBody); // this
            for (ObjectVar arg : args) {
                arg.load(methodBody);
            }
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invoke", AsmUtil.addArgsDesc(methodType, Type.getType(Object.class), true).getDescriptor(), false);
            if (result != null) result.store(methodBody);
        });
    }

    private ObjectVar initResultVar(MethodBody methodBody) {
        ObjectVar objectVar = null;
        if (methodType.getReturnType().getSort() != Type.VOID) {
            objectVar = new ObjectVar(methodBody.lvbIndex++, methodType.getReturnType().getDescriptor());
        }
        return objectVar;
    }
}
