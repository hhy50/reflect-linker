package io.github.hhy50.linker.syslinker;

import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.annotations.Target;

/**
 * The interface Default impl linker.
 */
@Target.Bind("io.github.hhy50.linker.define.provider.DefaultTargetProviderImpl")
public interface DefaultImplLinker {

    /**
     * New instance object.
     *
     * @param target the target
     * @return the object
     */
    @Method.Constructor
    Object newInstance(Object target);
}
