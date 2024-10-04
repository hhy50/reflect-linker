package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Objects;

/**
 * The type Method invoke action.
 */
public class MethodInvokeAction implements TypedAction {

    protected MethodDescriptor methodDescriptor;
    protected Action instance;
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
        mv.visitMethodInsn(getOpCode(),
                owner, methodDescriptor.getMethodName(), methodDescriptor.getDesc(), false);
    }

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

    public static MethodInvokeAction invokeSuper() {
        return invokeSuper(null, null);
    }

    public static MethodInvokeAction invokeSuper(String superOwner) {
        return invokeSuper(superOwner, null);
    }

    public static MethodInvokeAction invokeSuper(MethodDescriptor md) {
        return invokeSuper(null, md);
    }

    public static MethodInvokeAction invokeSuper(String superOwner, MethodDescriptor md) {
        return new InvokeSupper(superOwner, md);
    }

    /**
     * invokeSuper method
     */
    static class InvokeSupper extends MethodInvokeAction {

        private String superOwner;

        /**
         * @param superOwner
         * @param md
         */
        public InvokeSupper(String superOwner, MethodDescriptor md) {
            super(md);
            this.instance = LoadAction.LOAD0;
            this.superOwner = superOwner;
        }

        @Override
        public void apply(MethodBody body) {
            if (args == null) {
                args = body.getArgs();
            }
            if (this.superOwner == null) {
                this.superOwner = body.getClassBuilder().getSuperOwner();
            }
            if (this.methodDescriptor == null) {
                this.methodDescriptor = body.getMethodDescriptor();
            }
            if (!Objects.equals(methodDescriptor.getOwner(), superOwner)) {
                methodDescriptor.setOwner(superOwner);
            }
            super.apply(body);
        }

        public int getOpCode() {
            return Opcodes.INVOKESPECIAL;
        }

        @Override
        public MethodInvokeAction setInstance(Action instance) {
            throw new UnsupportedOperationException("InvokeSupper() method not support set invoke object");
        }
    }
}
