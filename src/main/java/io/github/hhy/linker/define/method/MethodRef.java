package io.github.hhy.linker.define.method;

import io.github.hhy.linker.define.field.FieldRef;

public class MethodRef {
    private FieldRef owner;
    private String name;
    private String[] args;
    private String returnType;

    public FieldRef getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String[] getArgs() {
        return args;
    }

    public String getFullName() {
        return owner.getFullName() + "_$$_" + name;
    }
}
