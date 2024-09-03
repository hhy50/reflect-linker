package io.github.hhy.linker.define.method;

import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.generate.bytecode.vars.ObjectVar;
import org.objectweb.asm.Type;


public class MethodRef {
    protected FieldRef owner;

    /**
     * 方法名字
     */
    protected String name;

    /**
     * 指定调用的super
     */
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
        return owner.getUniqueName()+"_$$_"+name;
    }

    public String getSuperClass() {
        return superClass;
    }

    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    public Type[] getArgsType() {
        return new Type[] {ObjectVar.TYPE};
    }
}
