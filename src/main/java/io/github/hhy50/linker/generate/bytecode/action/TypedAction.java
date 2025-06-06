package io.github.hhy50.linker.generate.bytecode.action;

import org.objectweb.asm.Type;

/**
 * The interface Action.
 */
public interface TypedAction extends Action {

    /**
     * Get action result type
     *
     * @return type type
     */
    Type getType();

    /**
     * Return this action result
     *
     * @return action action
     */
    default Action thenReturn() {
        return block ->  {
            apply(block);
            // 有些类型执行apply（也就是有了body之后）后才能确定
            Actions.areturn(getType()).apply(block);
        };
    }
}
