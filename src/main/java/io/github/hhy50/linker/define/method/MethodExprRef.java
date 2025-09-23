package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.generate.ArgsDepAnalysis;
import io.github.hhy50.linker.generate.invoker.Invoker;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.List;

public class MethodExprRef extends MethodRef {

    private final Method method;
    private final List<MethodRef> methodRefs;
    private final ArgsDepAnalysis argsDepAnalysis;

    /**
     * Instantiates a new Method ref.
     *
     */
    public MethodExprRef(Method method, List<MethodRef> methodRefs, ArgsDepAnalysis argsDepAnalysis) {
        super(method.getName(), method.getName());
        this.method = method;
        this.methodRefs = methodRefs;
        this.argsDepAnalysis = argsDepAnalysis;
    }

    @Override
    public Type getMethodType() {
        return Type.getMethodType(argsDepAnalysis.getReturnType(), argsDepAnalysis.getArgsType());
    }

    @Override
    public Invoker<?> defineInvoker() {
        return null;
    }
}
