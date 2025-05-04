package io.github.hhy50.linker.define.field;


import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import org.objectweb.asm.Type;

/**
 * The type Field ref.
 */
public abstract class FieldRef {

    private String objName;

    /**
     * The Field name.
     */
    public String fieldName;

    private String fullName;

    private FieldRef prev;

    /**
     * Instantiates a new Field ref.
     *
     * @param prev    the prev
     * @param objName the obj name
     * @param name    the name
     */
    public FieldRef(FieldRef prev, String objName, String name) {
        this.prev = prev;
        this.objName = objName;
        this.fieldName = name;
    }

    /**
     * Gets unique name.
     *
     * @return the unique name
     */
    public String getUniqueName() {
        String prefix = "";
        if (prev != null) {
            prefix = prev.getUniqueName()+"_$_";
        }
        return prefix+fieldName;
    }

    /**
     * Gets getter name.
     *
     * @return the getter name
     */
    public String getGetterName() {
        return getUniqueName()+"_getter_mh";
    }

    /**
     * Gets setter name.
     *
     * @return the setter name
     */
    public String getSetterName() {
        return getUniqueName()+"_setter_mh";
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
     * Gets prev.
     *
     * @return the prev
     */
    public FieldRef getPrev() {
        return prev;
    }

    /**
     * Sets full name.
     *
     * @param fullName the full name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets full name.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }
}
