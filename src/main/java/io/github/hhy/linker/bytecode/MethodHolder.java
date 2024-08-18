package io.github.hhy.linker.bytecode;

public class MethodHolder {

    public final String owner;
    public final String methodName;
    public final String desc;

    public MethodHolder(String owner, String methodName, String desc) {
        this.owner = owner;
        this.methodName = methodName;
        this.desc = desc;
    }
}
