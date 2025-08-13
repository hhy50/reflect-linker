package io.github.hhy50.linker.define.field;


import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import org.objectweb.asm.Type;

/**
 * The type Field ref.
 */
public abstract class FieldRef {

    /**
     * The Field name.
     */
    public String fieldName;

    /**
     * The Full name.
     */
    protected String fullName;

    /**
     * The Prev.
     */
    protected FieldRef prev;

    /**
     * The Nullable.
     */
    protected boolean nullable;

    /**
     * The Default value.
     */
    protected String defaultValue;

    /**
     * Instantiates a new Field ref.
     *
     * @param prev the prev
     * @param name the name
     */
    public FieldRef(FieldRef prev, String name) {
        this.prev = prev;
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

    /**
     * Gets actual type.
     *
     * @return the actual type
     */
    public Class<?> getActualType() {
        return Object.class;
    }

    /**
     * Is nullable boolean.
     *
     * @return the boolean
     */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * Sets nullable.
     *
     * @param nullable the nullable
     */
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    /**
     * Sets default value.
     *
     * @param defaultValue the default value
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Gets default value.
     *
     * @return the default value
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        return super.equals(obj);
    }
}
