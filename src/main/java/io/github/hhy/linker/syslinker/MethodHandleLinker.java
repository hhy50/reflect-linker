package io.github.hhy.linker.syslinker;

import io.github.hhy.linker.annotations.Method;
import io.github.hhy.linker.annotations.Runtime;
import io.github.hhy.linker.annotations.Static;
import io.github.hhy.linker.annotations.Target;

import java.lang.invoke.MethodHandles;

@Runtime
@Target.Bind(value = "java.lang.invoke.MethodHandle")
public interface MethodHandleLinker {

    /**
     *
     * @param targetClass
     * @param caller
     * @return
     * @throws IllegalAccessException
     */
    @Static
    @Method.Name("privateLookupIn")
    MethodHandles.Lookup privateLookupIn(Class<?> targetClass, MethodHandles.Lookup caller) throws IllegalAccessException;
}
