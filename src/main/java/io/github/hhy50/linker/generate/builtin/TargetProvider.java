package io.github.hhy50.linker.generate.builtin;

/**
 * The interface Target provider.
 */

import io.github.hhy50.linker.annotations.Builtin;

/**
 * The interface Target provider.
 *
 * @param <T> the type parameter
 */
@Builtin
public interface TargetProvider<T> {

    /**
     * Gets target.
     *
     * @return the target
     */
    public T getTarget();
}
