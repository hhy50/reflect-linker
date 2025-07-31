package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.generate.ArgsDepAnalysis;
import io.github.hhy50.linker.generate.invoker.Invoker;
import io.github.hhy50.linker.generate.invoker.MethodExprInvoker;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.List;

public class MethodExprRef extends MethodRef {

    private final Method method;
    private final List<MethodRef> statement;
    private final ArgsDepAnalysis argsDepAnalysis;

    /**
     * Instantiates a new Method ref.
     *
     */
    public MethodExprRef(Method method, List<MethodRef> statement, ArgsDepAnalysis argsDepAnalysis) {
        super(null, null, "expr_"+COUNTER.incrementAndGet());
        this.method = method;
        this.statement = statement;
        this.argsDepAnalysis = argsDepAnalysis;
    }

    public List<MethodRef> getStatement() {
        return statement;
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
