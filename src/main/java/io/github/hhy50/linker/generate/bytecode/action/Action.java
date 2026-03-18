package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;

import static io.github.hhy50.linker.generate.bytecode.action.Actions.of;

/**
 * The interface Action.
 */
public interface Action {

    /**
     * Apply.
     *
     * @param body the body
     */
    void apply(MethodBody body);

    /**
     * On after action.
     *
     * @param after the after
     * @return the action
     */
    default Action andThen(Action after) {
        return of(this, after);
    }
}
