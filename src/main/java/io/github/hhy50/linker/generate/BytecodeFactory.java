package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.define.AbsMethod;
import io.github.hhy50.linker.generate.invoker.InvokerDecorator;



/**
 * The type Bytecode factory.
 */
public class BytecodeFactory {

    /**
     * Generate invoker method handle.
     *
     * @return the method handle
     */
    public static MethodHandle generateInvoker(AbsMethod absMethod) {
        return new InvokerDecorator(null, absMethod.getMetadata());
    }
}
