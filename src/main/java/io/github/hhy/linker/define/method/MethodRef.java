package io.github.hhy.linker.define.method;

import io.github.hhy.linker.define.field.FieldRef;


public class MethodRef {
    protected FieldRef owner;
    protected String name;
    protected String superClass;

    public MethodRef(FieldRef owner, String name) {
        this.owner = owner;
        this.name = name;
    }

    public FieldRef getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return owner.getFullName()+"_$$_"+name;
    }
}
