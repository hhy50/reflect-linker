package io.github.hhy50.linker.token;


import io.github.hhy50.linker.util.ReflectUtil;

import java.lang.reflect.Field;

/**
 * The type Field token.
 */
public class FieldToken implements Token {

    /**
     * The Field name.
     */
    public String fieldName;

    private IndexToken index;

    /**
     * Instantiates a new Field token.
     *
     * @param fieldName the field name
     */
    public FieldToken(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String value() {
        return fieldName;
    }

    @Override
    public String toString() {
        return fieldName+(index == null ? "" : index.toString());
    }

    /**
     * Gets field.
     *
     * @param owner the owner
     * @return the field
     */
    public Field getField(Class<?> owner) {
        String fieldName = value();
        return ReflectUtil.getField(owner, fieldName);
    }

    public void setIndex(IndexToken index) {
        this.index = index;
    }
}
