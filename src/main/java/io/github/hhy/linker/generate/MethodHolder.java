package io.github.hhy.linker.generate;

public class MethodHolder {

    private final String owner;
    private final String methodName;
    private final String methodDesc;

    public MethodHolder(String owner, String methodName, String methodDesc) {
        this.owner = owner;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    public String getOwner() {
        return owner;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getDesc() {
        return methodDesc;
    }
}
