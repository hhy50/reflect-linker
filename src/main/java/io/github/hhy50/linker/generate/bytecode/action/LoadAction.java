package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.Opcodes;

/**
 * 变量加载的操作
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public interface LoadAction extends Action {

    Action LOAD0 = new LoadAction() {
        @Override
        public void load(MethodBody body) {
            body.getWriter().visitVarInsn(Opcodes.ALOAD, 0);
        }
    };

    /** {@inheritDoc} */
    @Override
    default void apply(MethodBody body) {
        load(body);
    }

    /**
     * <p>ifNull.</p>
     *
     * @param ifBlock a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     */
    default Action ifNull(Action ifBlock) {
        return ifNull(ifBlock, null);
    }

    /**
     * <p>ifNull.</p>
     *
     * @param ifBlock a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     * @param elseBlock a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     */
    default Action ifNull(Action ifBlock, Action elseBlock) {
        return new ConditionJumpAction(Condition.isNull(this), ifBlock, elseBlock);
    }

    /**
     * <p>load.</p>
     *
     * @param body a {@link io.github.hhy50.linker.generate.MethodBody} object.
     */
    void load(MethodBody body);
}
