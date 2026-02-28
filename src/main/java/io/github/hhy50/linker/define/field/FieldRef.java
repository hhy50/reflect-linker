package io.github.hhy50.linker.define.field;


import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import org.objectweb.asm.Type;

import java.util.List;

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

    private boolean nullable;

    private Object defaultValue;

    private List<Object> indexs;

    /**
     * Instantiates a new Field ref.
     *
     * @param fullName the fullName
     * @param name     the name
     */
    public FieldRef(String fullName, String name) {
        this.fullName = fullName;
        this.name = name;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
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

    /**
     * Sets static.
     *
     * @param isStatic the is static
     */
    public void setStatic(boolean isStatic) {

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
     * Is nullable boolean.
     *
     * @return the boolean
     */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * Sets index.
     *
     * @param indexs the indexs
     */
    public void setIndex(List<Object> indexs) {
        this.indexs = indexs;
    }

    /**
     * Gets indexs.
     *
     * @return the indexs
     */
    public List<Object> getIndexs() {
        return indexs;
    }
}