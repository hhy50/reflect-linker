package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.define.AbsMethod;
import io.github.hhy50.linker.define.method.MethodExprRef;
import io.github.hhy50.linker.generate.invoker.InvokerDecorator;



/**
 * The type Bytecode factory.
 */
public class BytecodeFactory {

    /**
     * Generate invoker method handle.
     *
     * @param classBuilder    the class builder
     * @return the method handle
     */
    public static MethodHandle generateInvoker(InvokeClassImplBuilder classBuilder, AbsMethod absMethod) {
        return new InvokerDecorator();
    }
}
