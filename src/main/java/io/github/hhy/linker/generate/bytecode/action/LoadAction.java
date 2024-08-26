package io.github.hhy.linker.generate.bytecode.action;

import io.github.hhy.linker.generate.MethodBody;
import org.objectweb.asm.Opcodes;

/**
 * 变量加载的操作
 */
public interface LoadAction extends Action {

    Action LOAD0 = new LoadAction() {
        @Override
        public void load(MethodBody body) {
            body.getWriter().visitVarInsn(Opcodes.ALOAD, 0);
        }
    };

    @Override
    default void apply(MethodBody body) {
        load(body);
    }

    default void ifNull(MethodBody body, Action ifBlock) {
        body.append(() ->
                new ConditionJumpAction(Condition.isNull(this), ifBlock, null)
        );
    }

    default void ifNull(MethodBody body, Action ifBlock, Action elseBlock) {
        body.append(() ->
                new ConditionJumpAction(Condition.isNull(this), ifBlock, elseBlock)
        );
    }

    void load(MethodBody body);
}
