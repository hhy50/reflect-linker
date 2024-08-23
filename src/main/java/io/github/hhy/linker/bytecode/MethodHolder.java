package io.github.hhy.linker.bytecode;

public class MethodHolder {

    private final String owner;
    private final String methodName;
    private final String desc;

    public MethodHolder(String owner, String methodName, String desc) {
        this.owner = owner;
        this.methodName = methodName;
        this.desc = desc;
    }

    public String getOwner() {
        return owner;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getDesc() {
        return desc;
    }
}
