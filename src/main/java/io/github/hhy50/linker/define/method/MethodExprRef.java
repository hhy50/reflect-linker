package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.generate.ArgsDepAnalysis;
import io.github.hhy50.linker.generate.invoker.MethodExprInvoker;
import io.github.hhy50.linker.token.ArgsToken;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MethodExprRef extends MethodRef {

    private final Method method;
    private final List<MethodRef> stepMethods;
    private final ArgsDepAnalysis argsDepAnalysis;

    /**
     * Instantiates a new Method ref.
     *
     */
    public MethodExprRef(Method method) {
        super(method.getName(), method.getName());
        this.method = method;
        this.stepMethods = new ArrayList<>();
        this.argsDepAnalysis = new ArgsDepAnalysis();
    }

    public void addStepMethod(MethodRef methodRef, ArgsToken argsToken) {
        stepMethods.add(methodRef);
        argsDepAnalysis.analyse(methodRef.getMhType(), argsToken);
    }

    public List<MethodRef> getStepMethods() {
        return stepMethods;
    }

    public Type getMethodType() {
        Type rType = this.argsDepAnalysis.getReturnType();
        Type[] argsType = this.argsDepAnalysis.getArgsType();
        return Type.getMethodType(rType, argsType);
    }

    @Override
    public Type getMhType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MethodExprInvoker defineInvoker() {
        return new MethodExprInvoker(this);
    }
}
