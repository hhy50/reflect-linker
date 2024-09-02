package io.github.hhy.linker.syslinker;

import io.github.hhy.linker.annotations.Method;
import io.github.hhy.linker.annotations.Target;

@Target.Bind("io.github.hhy.linker.define.provider.DefaultTargetProviderImpl")
public interface DefaultImplLinker {

    @Method.Constructor
    Object newInstance(Object target);
}
