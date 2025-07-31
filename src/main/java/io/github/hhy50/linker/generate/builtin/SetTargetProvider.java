package io.github.hhy50.linker.generate.builtin;


/**
 * The interface Reset target provider.
 *
 * @param <T> the type parameter
 */

import io.github.hhy50.linker.annotations.Builtin;

@Builtin
public interface SetTargetProvider<T> {


    /**
     * Sets target.
     *
     * @param target the target
     */
    void setValue(T target);
}
