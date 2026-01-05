package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.define.md.AbsMethodMetadata;
import io.github.hhy50.linker.generate.ArgsDepAnalysis;
import io.github.hhy50.linker.generate.invoker.MethodExprInvoker;
import io.github.hhy50.linker.token.ArgsToken;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

public class MethodExprRef extends MethodRef {

    private final AbsMethodMetadata metadata;
    private final List<MethodRef> stepMethods;
    private final ArgsDepAnalysis argsDepAnalysis;

    /**
     * Instantiates a new Method ref.
     *
     */
    public MethodExprRef(AbsMethodMetadata metadata) {
        super(metadata.getName(), metadata.getName());
        this.metadata = metadata;
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

    public AbsMethodMetadata getMetadata() {
        return this.metadata;
    }
}
