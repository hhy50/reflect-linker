package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.define.parameter.ParameterLoader;

public class MethodExprStep {

    private final MethodRef methodRef;
    private final ParameterLoader parameterLoader;

    public MethodExprStep(MethodRef methodRef, ParameterLoader parameterLoader) {
        this.methodRef = methodRef;
        this.parameterLoader = parameterLoader;
    }

    public MethodRef getMethodRef() {
        return methodRef;
    }

    public ParameterLoader getParameterLoader() {
        return parameterLoader;
    }
}
