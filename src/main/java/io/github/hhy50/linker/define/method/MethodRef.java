package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.define.field.FieldRef;
import org.objectweb.asm.Type;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * The type Method ref.
 */
public abstract class MethodRef {
    protected static final AtomicInteger COUNTER = new AtomicInteger(0);

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
     * The Full name.
     */
    protected String uniqueName;

    /**
     * Instantiates a new Method ref.
     *
     * @param owner the owner
     * @param name  the name
     */
    public MethodRef(FieldRef owner, String name) {
        this.owner = owner;
        this.name = name;
        this.uniqueName = owner.getUniqueName()+"_$$_"+name+"_"+COUNTER.getAndIncrement();
    }

    /**
     * Instantiates a new Method ref.
     *
     * @param owner the owner
     * @param name  the name
     * @param uniqueName  the uniqueName
     */
    public MethodRef(FieldRef owner, String name, String uniqueName) {
        this.owner = owner;
        this.name = name;
        this.uniqueName = uniqueName;
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
     * Gets unique name.
     *
     * @return the full name
     */
    public String getUniqueName() {
        return uniqueName;
    }

    /**
     * Gets invoker name.
     *
     * @return the invoker name
     */
    public String getInvokerName() {
        return getUniqueName()+"_invoker_mh";
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
     * Gets method type.
     *
     * @return the method type
     */
    public abstract Type getMethodType();

//    /**
//     * Get args type type [ ].
//     *
//     * @return the type [ ]
//     */
//    public abstract Type[] getArgsType();
}
