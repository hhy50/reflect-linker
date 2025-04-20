package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static java.util.Objects.requireNonNull;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;

/**
 * The type Method invoke action.
 */
public class MethodInvokeAction implements LoadAction {

    /**
     * The Method descriptor.
     */
    protected final MethodDescriptor descriptor;
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
     * @param descriptor the method holder
     */
    public MethodInvokeAction(MethodDescriptor descriptor) {
        this.descriptor = descriptor;
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

        int opCode = getOpCode();
        mv.visitMethodInsn(opCode,
                descriptor.getOwner(), descriptor.getMethodName(), descriptor.getDesc(), opCode == INVOKEINTERFACE);
    }

    @Override
    public void load(MethodBody body) {
        this.apply(body);
    }

    /**
     * Gets op code.
     *
     * @return the op code
     */
    public int getOpCode() {
        if (descriptor.getMethodName().equals("<init>")) {
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
        requireNonNull(this.descriptor);
        return descriptor.getReturnType();
    }
}
