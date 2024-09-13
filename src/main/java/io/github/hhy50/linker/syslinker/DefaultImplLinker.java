package io.github.hhy50.linker.syslinker;

import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.annotations.Target;

/**
 * <p>DefaultImplLinker interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
@Target.Bind("io.github.hhy50.linker.define.provider.DefaultTargetProviderImpl")
public interface DefaultImplLinker {

    /**
     * <p>newInstance.</p>
     *
     * @param target a {@link java.lang.Object} object.
     * @return a {@link java.lang.Object} object.
     */
    @Method.Constructor
    Object newInstance(Object target);
}
