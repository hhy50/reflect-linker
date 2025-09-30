package io.github.hhy50.linker.define;

import io.github.hhy50.linker.generate.MethodHandle;
import org.objectweb.asm.Type;

public interface MethodHandleProvider {
    /**
     * Define invoker invoker.
     *
     * @return the invoker
     */
    public MethodHandle defineInvoker();

    /**
     * Get method type.
     *
     * @return
     */
    Type getMhType();
}
