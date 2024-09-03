package io.github.hhy.linker.define.method;


import io.github.hhy.linker.define.field.FieldRef;


public class RuntimeMethodRef extends MethodRef {
    private String[] argsType;
    public RuntimeMethodRef(FieldRef owner, String name, String[] argsType) {
        super(owner, name);
        this.argsType = argsType;
    }
}
