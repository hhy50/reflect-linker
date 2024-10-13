package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;

/**
 * The type Method invoke action.
 */
public class MethodInvokeAction implements TypedAction {

    /**
     * The Method descriptor.
     */
    protected MethodDescriptor methodDescriptor;
    /**
     * The Instance.
     */
    protected Action instance;
    /**
     * The Args.
     */
    protected Action[] args;

    /**
     * Instantiates a new Method invoke action.
     *
     * @param methodDescriptor the method holder
     */
    public MethodInvokeAction(MethodDescriptor methodDescriptor) {
        this.methodDescriptor = methodDescriptor;
    }

    @Override
    public void apply(MethodBody body) {
        MethodVisitor mv = body.getWriter();
        if (instance != null) {
            instance.apply(body);
        }
        for (int i = 0; i < (args == null ? 0 : args.length); i++) {
            args[i].apply(body);
        }

        String owner = methodDescriptor.getOwner();
        if (owner == null) {
            if (instance != null && instance instanceof TypedAction) {
                owner = ((TypedAction) instance).getType().getInternalName();
            } else {
                owner = body.getClassBuilder().getClassOwner();
            }
        }
        int opCode = getOpCode();
        mv.visitMethodInsn(opCode,
                owner, methodDescriptor.getMethodName(), methodDescriptor.getDesc(), opCode == INVOKEINTERFACE);
    }

    /**
     * Gets op code.
     *
     * @return the op code
     */
    public int getOpCode() {
        if (methodDescriptor.getMethodName().equals("<init>")) {
            return Opcodes.INVOKESPECIAL;
        }
        return instance != null ? Opcodes.INVOKEVIRTUAL : Opcodes.INVOKESTATIC;
    }

    /**
     * Sets instance.
     *
     * @param instance the instance
     * @return the instance
     */
    public MethodInvokeAction setInstance(Action instance) {
        this.instance = instance;
        return this;
    }

    /**
     * Sets args.
     *
     * @param args the args
     * @return the args
     */
    public MethodInvokeAction setArgs(Action... args) {
        this.args = args;
        return this;
    }

    @Override
    public Type getType() {
        return Type.getMethodType(methodDescriptor.getDesc()).getReturnType();
    }
}
