package io.github.hhy.linker.define;

import java.util.List;

public class MethodDefine {
    private String methodName;
    private String methodDesc;
    private List<String> parameters;
    private String returnType;
    private boolean invokeSuper;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodDesc() {
        return methodDesc;
    }

    public void setMethodDesc(String methodDesc) {
        this.methodDesc = methodDesc;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public boolean isInvokeSuper() {
        return invokeSuper;
    }

    public void setInvokeSuper(boolean invokeSuper) {
        this.invokeSuper = invokeSuper;
    }
}
