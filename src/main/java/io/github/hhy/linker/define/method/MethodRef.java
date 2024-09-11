package io.github.hhy.linker.define.method;

import io.github.hhy.linker.define.field.FieldRef;
import org.objectweb.asm.Type;


/**
 * <p>Abstract MethodRef class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
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

    /**
     * <p>Constructor for MethodRef.</p>
     *
     * @param owner a {@link io.github.hhy.linker.define.field.FieldRef} object.
     * @param name a {@link java.lang.String} object.
     */
    public MethodRef(FieldRef owner, String name) {
        this.owner = owner;
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>owner</code>.</p>
     *
     * @return a {@link io.github.hhy.linker.define.field.FieldRef} object.
     */
    public FieldRef getOwner() {
        return owner;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>getFullName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFullName() {
        return owner.getUniqueName()+"_$$_"+name;
    }

    /**
     * <p>getInvokerName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getInvokerName() {
        return getFullName()+"_invoker_mh";
    }

    /**
     * <p>Getter for the field <code>superClass</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSuperClass() {
        return superClass;
    }

    /**
     * <p>Setter for the field <code>superClass</code>.</p>
     *
     * @param superClass a {@link java.lang.String} object.
     */
    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    /**
     * <p>getArgsType.</p>
     *
     * @return an array of {@link org.objectweb.asm.Type} objects.
     */
    public abstract Type[] getArgsType();
}
