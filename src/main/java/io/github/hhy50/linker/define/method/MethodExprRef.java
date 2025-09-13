package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.generate.ArgsDepAnalysis;
import io.github.hhy50.linker.generate.invoker.Invoker;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.List;

public class MethodExprRef extends MethodRef {

    private final Method method;
    private final List<Object> statements;
    private final ArgsDepAnalysis argsDepAnalysis;

    /**
     * Instantiates a new Method ref.
     *
     */
    public MethodExprRef(Method method, List<Object> statements, ArgsDepAnalysis argsDepAnalysis) {
        super(method.getName(), method.getName());
        this.method = method;
        this.statements = statements;
        this.argsDepAnalysis = argsDepAnalysis;
    }

    // public List<Object> getStatement() {
    //     return statements;
    // }

    @Override
    public Type getMethodType() {
        return Type.getMethodType(argsDepAnalysis.getReturnType(), argsDepAnalysis.getArgsType());
    }

    @Override
    public Invoker<?> defineInvoker() {
        return null;
    }
}
