package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.SmartMethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.utils.Args;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static io.github.hhy50.linker.generate.bytecode.action.Actions.of;
import static java.util.Objects.requireNonNull;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;

/**
 * The type Smart method invoke action.
 */
public class SmartMethodInvokeAction extends MethodInvokeAction implements LazyTypedAction, LoadAction {

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
    public Action load() {
        return Actions.of(instance, this.getArgs(), body -> {
            MethodDescriptor descriptor = getMethodDescriptor(body);
            String owner = descriptor.getOwner();
            if (owner == null || owner.equals(SmartMethodDescriptor.EMPTY_NAME)) {
                if (instance != null && instance instanceof TypedAction) {
                    owner = ((TypedAction) instance).getType().getInternalName();
                } else {
                    owner = body.getClassBuilder().getClassOwner();
                }
            }
            MethodVisitor mv = body.getWriter();
            int opCode = getOpCode();
            mv.visitMethodInsn(opCode,
                    owner, descriptor.getMethodName(), descriptor.getDesc(), opCode == INVOKEINTERFACE);
        });
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
            this.smartDescriptor = new SmartMethodDescriptor(body.getMethodBuilder().getDescriptor());
        return this.smartDescriptor;
    }

    /**
     * Get args action [ ].
     *
     * @return the action [ ]
     */
    public Action getArgs() {
        if (args != null) return of(args);
        if (this.descriptor == null) {
            return Args.loadArgs();
        }
        return Actions.empty();
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
