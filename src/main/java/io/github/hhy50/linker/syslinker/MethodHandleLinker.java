package io.github.hhy50.linker.syslinker;

import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.annotations.Runtime;
import io.github.hhy50.linker.annotations.Static;
import io.github.hhy50.linker.annotations.Target;

import java.lang.invoke.MethodHandles;

/**
 * <p>MethodHandleLinker interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
@Runtime
@Target.Bind(value = "java.lang.invoke.MethodHandle")
public interface MethodHandleLinker {

    /**
     * <p>privateLookupIn.</p>
     *
     * @param targetClass a {@link java.lang.Class} object.
     * @param caller a {@link java.lang.invoke.MethodHandles.Lookup} object.
     * @return a {@link java.lang.invoke.MethodHandles.Lookup} object.
     * @throws java.lang.IllegalAccessException if any.
     */
    @Static
    @Method.Name("privateLookupIn")
    MethodHandles.Lookup privateLookupIn(Class<?> targetClass, MethodHandles.Lookup caller) throws IllegalAccessException;
}
