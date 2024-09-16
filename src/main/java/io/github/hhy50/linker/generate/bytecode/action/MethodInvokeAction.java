package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.entity.MethodHolder;
import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * <p>MethodInvokeAction class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class MethodInvokeAction implements Action {

    private final MethodHolder methodHolder;
    private Action instance;
    private Action[] args;

    /**
     * <p>Constructor for MethodInvokeAction.</p>
     *
     * @param methodHolder a {@link io.github.hhy50.linker.entity.MethodHolder} object.
     */
    public MethodInvokeAction(MethodHolder methodHolder) {
        this.methodHolder = methodHolder;
        this.args = new LoadAction[0];
    }

    /** {@inheritDoc} */
    @Override
    public void apply(MethodBody body) {
        MethodVisitor mv = body.getWriter();
        if (instance != null) {
            instance.apply(body);
        }
        for (Action arg : args) {
            arg.apply(body);
        }
        mv.visitMethodInsn(instance != null ? Opcodes.INVOKEVIRTUAL : Opcodes.INVOKESTATIC,
                methodHolder.getOwner(), methodHolder.getMethodName(), methodHolder.getDesc(), false);
    }

    /**
     * <p>Setter for the field <code>instance</code>.</p>
     *
     * @param instance a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     * @return a {@link io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction} object.
     */
    public MethodInvokeAction setInstance(Action instance) {
        this.instance = instance;
        return this;
    }

    /**
     * <p>Setter for the field <code>args</code>.</p>
     *
     * @param args a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     * @return a {@link io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction} object.
     */
    public MethodInvokeAction setArgs(Action... args) {
        this.args = args;
        return this;
    }
}
