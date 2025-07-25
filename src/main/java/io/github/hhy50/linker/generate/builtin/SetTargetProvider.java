package io.github.hhy50.linker.generate.builtin;


import io.github.hhy50.linker.annotations.Generate;

/**
 * The interface Reset target provider.
 *
 * @param <T> the type parameter
 */
@Generate.Builtin
public interface SetTargetProvider<T> {


    /**
     * Sets target.
     *
     * @param target the target
     */
    void setValue(T target);
}
