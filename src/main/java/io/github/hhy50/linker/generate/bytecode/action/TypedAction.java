package io.github.hhy50.linker.generate.bytecode.action;

import org.objectweb.asm.Type;

/**
 * The interface Action.
 */
public interface TypedAction extends Action {

    /**
     * Get action result type
     *
     * @return type
     */
    Type getType();
}
