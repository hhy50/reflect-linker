package io.github.hhy50.linker.token;

import io.github.hhy50.linker.util.ReflectUtil;

import java.lang.reflect.Field;

/**
 * The type Token.
 */
public abstract class Token {

    /**
     * The Next.
     */
    public Token next;

    /**
     * Value string.
     *
     * @return the string
     */
    public abstract String value();

    /**
     * Array expr boolean.
     *
     * @return the boolean
     */
    public boolean arrayExpr() {
        return false;
    }

    /**
     * Map expr boolean.
     *
     * @return the boolean
     */
    public boolean mapExpr() {
        return false;
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
}
