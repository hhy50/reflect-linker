package io.github.hhy.linker.generate.bytecode;


import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.constant.MethodHandle;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
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
    public MethodHandleMember(int access, String owner, String mhVarName, Type methodType) {
        super(access, owner, mhVarName, MethodHandle.TYPE);
        this.methodType = methodType;
    }

    public VarInst invoke(MethodBody methodBody, VarInst that, VarInst... args) {
        ObjectVar result = initResultVar(methodBody);
        methodBody.append(mv -> {
            // if (mh.getClass().getName().contains("DirectMethodHandle$StaticAccessor"))
            getClassName(methodBody);
            mv.visitLdcInsn("DirectMethodHandle$StaticAccessor");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z", false);
            mv.visitJumpInsn(Opcodes.IFEQ, invokeThisLabel);

            // if static
            invokeStatic(result, methodBody, args);
            mv.visitJumpInsn(Opcodes.GOTO, invokeEndLabel);

            // else no static
            invokeInstance(result, methodBody, that, args);
            mv.visitLabel(invokeEndLabel);
        });
        return result;
    }

    public VarInst invokeStatic(MethodBody methodBody, VarInst... args) {
        ObjectVar result = initResultVar(methodBody);
        methodBody.append(mv -> {
            invokeStatic(result, methodBody, args);
        });
        return result;
    }

    public VarInst invokeInstance(MethodBody methodBody, VarInst that, VarInst... args) {
        ObjectVar result = initResultVar(methodBody);
        methodBody.append(mv -> {
            invokeInstance(result, methodBody, that, args);
        });
        return result;
    }

    private void invokeStatic(ObjectVar result, MethodBody methodBody, VarInst... args) {
        methodBody.append(write -> {
            load(methodBody); // mh
            for (VarInst arg : args) {
                arg.load(methodBody);
            }
            write.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invoke", methodType.getDescriptor(), false);
            if (result != null) result.store(methodBody);
        });
    }

    private void invokeInstance(ObjectVar result, MethodBody methodBody, VarInst that, VarInst... args) {
        methodBody.append(mv -> {
            mv.visitLabel(invokeThisLabel);
            that.checkNullPointer(methodBody, that.getName());

            load(methodBody); // mh
            that.load(methodBody); // this
            for (VarInst arg : args) {
                arg.load(methodBody);
            }
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invoke", AsmUtil.addArgsDesc(methodType, Type.getType(Object.class), true).getDescriptor(), false);
            if (result != null) result.store(methodBody);
        });
    }

    private ObjectVar initResultVar(MethodBody methodBody) {
        ObjectVar objectVar = null;
        if (methodType.getReturnType().getSort() != Type.VOID) {
            objectVar = new ObjectVar(methodBody.lvbIndex++, methodType.getReturnType());
        }
        return objectVar;
    }
}
