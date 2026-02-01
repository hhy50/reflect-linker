package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.define.md.AbsMethodMetadata;
import io.github.hhy50.linker.generate.ParameterTypeAnalysis;
import io.github.hhy50.linker.generate.invoker.MethodExprInvoker;
import io.github.hhy50.linker.util.RandomUtil;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

public class MethodExprRef extends MethodRef {

    private final AbsMethodMetadata metadata;
    private final List<MethodRef> stepMethods;
    private final ParameterTypeAnalysis parameterTypeAnalysis;
    private Type returnType = Type.VOID_TYPE;

    /**
     * Instantiates a new Method ref.
     *
     */
    public MethodExprRef(AbsMethodMetadata metadata) {
        super(metadata.getName());
        this.metadata = metadata;
        this.stepMethods = new ArrayList<>();
        this.parameterTypeAnalysis = new ParameterTypeAnalysis(metadata.getParameters());
    }

    public void addStepMethod(MethodRef methodRef) {
        stepMethods.add(methodRef);
        parameterTypeAnalysis.analyse(methodRef.getArgsIndexTable());
        this.returnType = methodRef.getGenericType().getReturnType();
    }

    public List<MethodRef> getStepMethods() {
        return stepMethods;
    }

    public Type getMethodType() {
        return Type.getMethodType(this.returnType, this.parameterTypeAnalysis.getParametersType());
    }

    @Override
    public Type getLookupType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFullName() {
        return "invoke_" + RandomUtil.getRandomString(5);
    }

    @Override
    public MethodExprInvoker defineInvoker() {
        return new MethodExprInvoker(this);
    }

    public AbsMethodMetadata getMetadata() {
        return this.metadata;
    }
}
