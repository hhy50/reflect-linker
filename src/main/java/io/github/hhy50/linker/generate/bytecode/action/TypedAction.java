package io.github.hhy50.linker.generate.bytecode.action;

import org.objectweb.asm.Type;

import static java.util.Objects.requireNonNull;

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
        return body -> {
            Type type = getType();
            requireNonNull(type);

            apply(body);
            Actions.areturn(type).apply(body);
        };
    }
}
