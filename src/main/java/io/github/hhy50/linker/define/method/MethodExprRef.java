package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.generate.ArgsDepAnalysis;
import io.github.hhy50.linker.generate.invoker.Invoker;
import io.github.hhy50.linker.generate.invoker.MethodExprInvoker;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.List;

public class MethodExprRef extends MethodRef {

    private final Method method;
    private final List<MethodRef> methods;
    private final ArgsDepAnalysis argsDepAnalysis;

    /**
     * Instantiates a new Method ref.
     *
     */
    public MethodExprRef(Method method, List<MethodRef> methods, ArgsDepAnalysis argsDepAnalysis) {
        super(null, null, "expr_"+COUNTER.incrementAndGet());
        this.method = method;
        this.methods = methods;
        this.argsDepAnalysis = argsDepAnalysis;
    }

    public List<MethodRef> getMethods() {
        return methods;
    }

    @Override
    public Type getMethodType() {
        return Type.getMethodType(argsDepAnalysis.getReturnType(), argsDepAnalysis.getArgsType());
    }

    @Override
    public Invoker<?> defineInvoker() {
        return new MethodExprInvoker(this);
    }
}
