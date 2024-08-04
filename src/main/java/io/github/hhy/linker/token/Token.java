package io.github.hhy.linker.token;

import io.github.hhy.linker.util.ReflectUtil;

import java.lang.reflect.Field;


public abstract class Token {

    public Token next;

    /**
     * 获取token值
     *
     * @return
     */
    public abstract String value();

    /**
     * 是否是array表达式
     * @return
     */
    public boolean arrayExpr() {
        return false;
    }

    /**
     * 是否是数组表达式
     * @return
     */
    public boolean mapExpr() {
        return false;
    }

    public Field getField(Class<?> owner) {
        String fieldName = value();
        return ReflectUtil.getField(owner, fieldName);
    }
}
