package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.define.md.AbsMethodMetadata;
import io.github.hhy50.linker.generate.ParameterTypeAnalysis;
import io.github.hhy50.linker.generate.invoker.MethodExprInvoker;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Method expr ref.
 */
public class MethodExprRef extends MethodRef {

    private final AbsMethodMetadata metadata;
    private final List<MethodRef> stepMethods;
    private final ParameterTypeAnalysis parameterTypeAnalysis;
    private Type returnType = Type.VOID_TYPE;

    /**
     * Instantiates a new Method ref.
     *
     * @param metadata the metadata
     */
    public MethodExprRef(AbsMethodMetadata metadata) {
        super(metadata.getName());
        this.metadata = metadata;
        this.stepMethods = new ArrayList<>();
        this.parameterTypeAnalysis = new ParameterTypeAnalysis(metadata.getParameters());
    }

    /**
     * Add step method.
     *
     * @param methodRef the method ref
     */
    public void addStepMethod(MethodRef methodRef) {
        stepMethods.add(methodRef);
        parameterTypeAnalysis.analyse(methodRef.getParametersLoader().analyse(methodRef.getGenericType()));
        this.returnType = methodRef.getReturnType();
    }

    /**
     * Gets step methods.
     *
     * @return the step methods
     */
    public List<MethodRef> getStepMethods() {
        return stepMethods;
    }

    /**
     * Gets method type.
     *
     * @return the method type
     */
    public Type getMethodType() {
        return Type.getMethodType(this.returnType, this.parameterTypeAnalysis.getParametersType());
    }

    @Override
    public Type getLookupType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MethodExprInvoker defineInvoker() {
        return new MethodExprInvoker(this);
    }

    /**
     * Gets metadata.
     *
     * @return the metadata
     */
    public AbsMethodMetadata getMetadata() {
        return this.metadata;
    }
}
