package io.github.hhy50.linker.generate.builtin;

import io.github.hhy50.linker.annotations.Generate;

/**
 * The interface Target provider.
 */

@Generate.Builtin
public interface TargetProvider<T> {

    /**
     * Gets target.
     *
     * @return the target
     */
    public T getTarget();
}
