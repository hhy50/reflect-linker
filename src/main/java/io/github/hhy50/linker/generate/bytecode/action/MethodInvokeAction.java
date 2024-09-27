package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * The type Method invoke action.
 */
public class MethodInvokeAction implements Action {

    private final MethodDescriptor methodDescriptor;
    private Action instance;
    private Action[] args;

    /**
     * Instantiates a new Method invoke action.
     *
     * @param methodDescriptor the method holder
     */
    public MethodInvokeAction(MethodDescriptor methodDescriptor) {
        this.methodDescriptor = methodDescriptor;
        this.args = new LoadAction[0];
    }

    @Override
    public void apply(MethodBody body) {
        MethodVisitor mv = body.getWriter();
        if (instance != null) {
            instance.apply(body);
        }
        for (Action arg : args) {
            arg.apply(body);
        }

        String owner = methodDescriptor.getOwner() == null ? body.getClassBuilder().getClassOwner() : methodDescriptor.getOwner();
        mv.visitMethodInsn(instance != null ? Opcodes.INVOKEVIRTUAL : Opcodes.INVOKESTATIC,
                owner, methodDescriptor.getMethodName(), methodDescriptor.getDesc(), false);
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
}
