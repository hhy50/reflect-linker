package io.github.hhy50.linker.generate.bytecode.vars;

import io.github.hhy50.linker.generate.bytecode.action.Action;

/**
 * The interface Class load action.
 */
public interface ClassTypeVarInst extends Action {

    /**
     * Gets lookup.
     *
     * @return lookup lookup
     */
    Action getLookup();
}
