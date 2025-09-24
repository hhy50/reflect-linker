package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.define.MethodHandleProvider;
import org.objectweb.asm.Type;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * The type Method ref.
 */
public abstract class MethodRef implements MethodHandleProvider {
    /**
     * The constant COUNTER.
     */
    protected static final AtomicInteger COUNTER = new AtomicInteger(0);

    /**
     * The Full name.
     */
    protected String fullName;

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
     * @param name  the name
     */
    public MethodRef(String fullName, String name) {
        this.fullName = fullName;
        this.name = name;
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
     * @return
     */
    public String getFullName() {
        return fullName;
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
}
