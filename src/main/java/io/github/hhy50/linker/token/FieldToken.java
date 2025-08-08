package io.github.hhy50.linker.token;


import io.github.hhy50.linker.util.ReflectUtil;

import java.lang.reflect.Field;
import java.util.List;

/**
 * The type Field token.
 */
public class FieldToken implements Token {

    /**
     * The Field name.
     */
    public String fieldName;

    /**
     * The Index.
     */
    public IndexToken index;

    public boolean nullable = false;

    /**
     * Instantiates a new Field token.
     *
     * @param fieldName the field name
     */
    public FieldToken(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Gets field.
     *
     * @param owner the owner
     * @return the field
     */
    public Field getField(Class<?> owner) {
        return ReflectUtil.getField(owner, fieldName);
    }

    public void setIndex(IndexToken index) {
        this.index = index;
    }

    /**
     * Gets index val.
     *
     * @return the index val
     */
    public List<Object> getIndexVal() {
        return index != null ? index.toValues() : null;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    @Override
    public String toString() {
        return fieldName+(index == null ? "" : index.toString())+(nullable ? "?" : "");
    }
}
