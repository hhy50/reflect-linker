package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.SmartMethodDescriptor;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.block.CodeBlock;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static java.util.Objects.requireNonNull;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;

/**
 * The type Smart method invoke action.
 */
public class SmartMethodInvokeAction extends MethodInvokeAction {

    /**
     * The Descriptor.
     */
    protected MethodDescriptor smartDescriptor;

    /**
     * Instantiates a new Smart method invoke action.
     *
     * @param descriptor the descriptor
     */
    public SmartMethodInvokeAction(MethodDescriptor descriptor) {
        super(descriptor);
    }

    @Override
    public void apply(CodeBlock block) {
        MethodVisitor mv = block.getWriter();
        if (instance != null) {
            instance.apply(block);
        }
        Action[] smartArgs = getArgs(block);
        for (int i = 0; i < (smartArgs == null ? 0 : smartArgs.length); i++) {
            smartArgs[i].apply(block);
        }

        MethodDescriptor descriptor = getMethodDescriptor(block);
        String owner = descriptor.getOwner();
        if (owner == null || owner.equals(SmartMethodDescriptor.EMPTY_NAME)) {
            if (instance != null && instance instanceof TypedAction) {
                owner = ((TypedAction) instance).getType().getInternalName();
            } else {
                owner = block.getClassBuilder().getClassOwner();
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
        if (this.descriptor != null)
            return this.descriptor;
        if (this.smartDescriptor == null)
            this.smartDescriptor = new SmartMethodDescriptor(body.getBuilder().getDescriptor());
        return this.smartDescriptor;
    }

    /**
     * Get args action [ ].
     *
     * @param body the body
     * @return the action [ ]
     */
    public Action[] getArgs(MethodBody body) {
        if (args != null) return args;
        if (this.descriptor == null) {
            return body.getArgs();
        }
        return new VarInst[0];
    }

    @Override
    public Type getType() {
        if (descriptor != null) {
            return super.getType();
        }
        requireNonNull(smartDescriptor);
        return Type.getMethodType(smartDescriptor.getDesc()).getReturnType();
    }
}
