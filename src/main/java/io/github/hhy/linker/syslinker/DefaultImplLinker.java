package io.github.hhy.linker.syslinker;

import io.github.hhy.linker.annotations.Method;
import io.github.hhy.linker.annotations.Target;

/**
 * <p>DefaultImplLinker interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
@Target.Bind("io.github.hhy.linker.define.provider.DefaultTargetProviderImpl")
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
