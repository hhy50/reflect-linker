package io.github.hhy.linker.define;


import io.github.hhy.linker.exceptions.VerifyException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 目标字段
 */

public abstract class Target$Field extends Target {
    /**
     *
     */
    @lombok.Getter
    private Field field;

    /**
     * 持有字段的类
     */
    @lombok.Getter
    private Class<?> owner;

    /**
     * 字段名
     */
    @lombok.Getter
    private String fieldName;

    /**
     * 是否是静态字段
     */
    @lombok.Getter
    private boolean isStatic;

    /**
     * 是否是私有字段
     */
    @lombok.Getter
    private boolean isPrivate;

    public Target$Field(Field field) {
        this.field = field;
        this.owner = field.getDeclaringClass();
        this.fieldName = field.getName();
        this.isStatic = (field.getModifiers() & Modifier.STATIC) > 0;
        this.isPrivate = (field.getModifiers() & Modifier.PRIVATE) > 0;
    }

    public static Target$Field createGetter(Field field) {
        return new Getter(field);
    }

    public static Target$Field createSetter(Field field) {
        return new Setter(field);
    }

    public static class Getter extends Target$Field {
        public Getter(Field field) {
            super(field);
        }
    }

    public static class Setter extends Target$Field {
        public Setter(Field field) {
            super(field);
        }
    }
}
