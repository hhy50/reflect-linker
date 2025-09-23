package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.define.AbsMethodDefine;
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
     * @param absMethodDefine the method define
     * @param methodRef       the method ref
     * @return the method handle
     */
    public static MethodHandle generateInvoker(InvokeClassImplBuilder classBuilder, AbsMethodDefine absMethodDefine, MethodExprRef methodRef) {
        return new InvokerDecorator(methodRef.defineInvoker(), absMethodDefine);
    }
}
