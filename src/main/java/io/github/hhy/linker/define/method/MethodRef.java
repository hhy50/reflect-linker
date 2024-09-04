package io.github.hhy.linker.define.method;

import io.github.hhy.linker.define.field.FieldRef;
import org.objectweb.asm.Type;


public abstract class MethodRef {
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

    public String getInvokerName() {
        return getFullName()+"_invoker_mh";
    }

    public String getSuperClass() {
        return superClass;
    }

    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    public abstract Type[] getArgsType();
}
