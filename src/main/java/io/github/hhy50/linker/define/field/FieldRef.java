package io.github.hhy50.linker.define.field;


import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import org.objectweb.asm.Type;

/**
 * The type Field ref.
 */
public abstract class FieldRef  {

    /**
     * The Field name.
     */
    protected String name;

    /**
     * The Full name.
     */
    protected String fullName;

    /**
     * Instantiates a new Field ref.
     *
     * @param fullName the fullName
     * @param name the name
     */
    public FieldRef(String fullName, String name) {
        this.fullName = fullName;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    /**
     * Gets full name.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public Type getType() {
        return ObjectVar.TYPE;
    }

    /**
     * Gets actual type.
     *
     * @return the actual type
     */
    public Class<?> getActualType() {
        return Object.class;
    }
}
