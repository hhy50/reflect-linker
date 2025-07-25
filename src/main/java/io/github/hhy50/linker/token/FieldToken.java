package io.github.hhy50.linker.token;


import io.github.hhy50.linker.define.ParseContext;
import io.github.hhy50.linker.util.ReflectUtil;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * The type Field token.
 */
public class FieldToken implements Token, ArgType {

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
        String fieldName = toString();
        return ReflectUtil.getField(owner, fieldName);
    }

    public void setIndex(IndexToken index) {
        this.index = index;
    }

    @Override
    public Type getType(ParseContext context, Method methodDefine) {
        return null;
    }
}
