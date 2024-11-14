package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.SmartMethodDescriptor;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;

/**
 * The type Smart method invoke action.
 */
public class SmartMethodInvokeAction extends MethodInvokeAction {

    /**
     * The Descriptor.
     */
    protected MethodDescriptor descriptor;

    /**
     * Instantiates a new Smart method invoke action.
     *
     * @param descriptor the descriptor
     */
    public SmartMethodInvokeAction(MethodDescriptor descriptor) {
        super(descriptor);
    }

    @Override
    public void apply(MethodBody body) {
        MethodVisitor mv = body.getWriter();
        if (instance != null) {
            instance.apply(body);
        }
        Action[] smartArgs = getArgs(body);
        for (int i = 0; i < (smartArgs == null ? 0 : smartArgs.length); i++) {
            smartArgs[i].apply(body);
        }

        MethodDescriptor descriptor = getMethodDescriptor(body);
        String owner = descriptor.getOwner();
        if (owner == null || owner.equals(SmartMethodDescriptor.EMPTY_NAME)) {
            if (instance != null && instance instanceof TypedAction) {
                owner = ((TypedAction) instance).getType().getInternalName();
            } else {
                owner = body.getClassBuilder().getClassOwner();
            }
        }
        int opCode = getOpCode();
        mv.visitMethodInsn(opCode,
                owner, descriptor.getMethodName(), descriptor.getDesc(), opCode == INVOKEINTERFACE);
    }

    /**
     * Gets method descriptor.
     *
     * @param body the body
     * @return the method descriptor
     */
    public MethodDescriptor getMethodDescriptor(MethodBody body) {
        if (this.methodDescriptor != null)
            return this.methodDescriptor;
        this.descriptor = new SmartMethodDescriptor(body.getMethodDescriptor());
        return this.descriptor;
    }

    /**
     * Get args action [ ].
     *
     * @param body the body
     * @return the action [ ]
     */
    public Action[] getArgs(MethodBody body) {
        if (args != null) return args;
        if (this.methodDescriptor == null) {
            return body.getArgs();
        }
        return new VarInst[0];
    }

    @Override
    public Type getType() {
        if (methodDescriptor != null) {
            return super.getType();
        }
        return Type.getMethodType(descriptor.getDesc()).getReturnType();
    }
}
