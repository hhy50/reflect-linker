package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.define.field.FieldRef;
import org.objectweb.asm.Type;


/**
 * The type Method ref.
 */
public abstract class MethodRef {
    /**
     * The Owner.
     */
    protected FieldRef owner;

    /**
     * The Name.
     */
    protected String name;

    /**
     * The Super class.
     */
    protected String superClass;

    /**
     * Instantiates a new Method ref.
     *
     * @param owner the owner
     * @param name  the name
     */
    public MethodRef(FieldRef owner, String name) {
        this.owner = owner;
        this.name = name;
    }

    /**
     * Gets owner.
     *
     * @return the owner
     */
    public FieldRef getOwner() {
        return owner;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets full name.
     *
     * @return the full name
     */
    public String getFullName() {
        return owner.getUniqueName()+"_$$_"+name+"_"+(System.nanoTime()%10000);
    }

    /**
     * Gets invoker name.
     *
     * @return the invoker name
     */
    public String getInvokerName() {
        return getFullName()+"_invoker_mh";
    }

    /**
     * Gets super class.
     *
     * @return the super class
     */
    public String getSuperClass() {
        return superClass;
    }

    /**
     * Sets super class.
     *
     * @param superClass the super class
     */
    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    /**
     * Get args type type [ ].
     *
     * @return the type [ ]
     */
    public abstract Type[] getArgsType();
}
