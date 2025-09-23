package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.generate.ArgsDepAnalysis;
import io.github.hhy50.linker.generate.invoker.Invoker;
import io.github.hhy50.linker.generate.invoker.MethodExprInvoker;
import io.github.hhy50.linker.token.ArgsToken;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.ArrayList;
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

    public MethodExprRef(Method method) {
        super(method.getName(), method.getName());
        this.method = method;
        this.methodRefs = new ArrayList<>();
        this.argsDepAnalysis = new ArgsDepAnalysis();
    }

    public void addStepMethod(MethodRef methodRef, ArgsToken argsToken) {
        methodRefs.add(methodRef);
        argsDepAnalysis.analyse(methodRef.getMethodType(), argsToken);
    }


    @Override
    public Type getMethodType() {
        return Type.getMethodType(argsDepAnalysis.getReturnType(), argsDepAnalysis.getArgsType());
    }

    @Override
    public Invoker<?> defineInvoker() {
        return new MethodExprInvoker(this, getMethodType());
    }
}
