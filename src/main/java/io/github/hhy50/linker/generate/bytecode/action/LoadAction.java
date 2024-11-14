package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * The interface Load action.
 */
public interface LoadAction extends TypedAction {

    /**
     * The constant LOAD0.
     */
    Action LOAD0 = body -> body.getWriter().visitVarInsn(Opcodes.ALOAD, 0);

    @Override
    default void apply(MethodBody body) {
        load(body);
    }

    /**
     * If null action.
     *
     * @param ifBlock the if block
     * @return the action
     */
    default Action ifNull(Action ifBlock) {
        return ifNull(ifBlock, null);
    }

    /**
     * If null action.
     *
     * @param ifBlock   the if block
     * @param elseBlock the else block
     * @return the action
     */
    default Action ifNull(Action ifBlock, Action elseBlock) {
        return new ConditionJumpAction(Condition.isNull(this), ifBlock, elseBlock);
    }

    /**
     * Of field.
     * @param field
     * @param type
     * @return
     */
    default FieldAction ofField(String field, Class<?> type) {
        return new FieldAction(this, field, Type.getType(type));
    }

    /**
     * Load.
     *
     * @param body the body
     */
    void load(MethodBody body);
}
