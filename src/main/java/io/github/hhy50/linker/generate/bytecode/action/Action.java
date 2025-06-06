package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.bytecode.block.CodeBlock;

/**
 * The interface Action.
 */
public interface Action {

    /**
     * Apply.
     *
     * @param block the body
     */
    void apply(CodeBlock block);

    /**
     * On after action.
     *
     * @param after the after
     * @return the action
     */
    default Action andThen(Action after) {
        return (block) -> {
            apply(block);
            after.apply(block);
        };
    }
}
