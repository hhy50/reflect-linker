package io.github.hhy50.linker.token;

import io.github.hhy50.linker.util.ReflectUtil;

import java.lang.reflect.Field;

/**
 * <p>Abstract Token class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public abstract class Token {

    public Token next;

    /**
     * 获取token值
     *
     * @return a {@link java.lang.String} object.
     */
    public abstract String value();

    /**
     * 是否是array表达式
     *
     * @return a boolean.
     */
    public boolean arrayExpr() {
        return false;
    }

    /**
     * 是否是数组表达式
     *
     * @return a boolean.
     */
    public boolean mapExpr() {
        return false;
    }

    /**
     * <p>getField.</p>
     *
     * @param owner a {@link java.lang.Class} object.
     * @return a {@link java.lang.reflect.Field} object.
     */
    public Field getField(Class<?> owner) {
        String fieldName = value();
        return ReflectUtil.getField(owner, fieldName);
    }
}
