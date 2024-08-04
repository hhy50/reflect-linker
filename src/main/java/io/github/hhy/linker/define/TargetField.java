package io.github.hhy.linker.define;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 目标字段
 */

public class TargetField extends TargetPoint {
    /**
     *
     */
    private Field field;

    /**
     * 持有字段的类
     */
    private Class<?> owner;

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 是否是静态字段
     */
    private boolean isStatic;

    /**
     * 是否是私有字段
     */
    private boolean isPrivate;

    /**
     * 上一个字段， 比如 a.b, this=b, prev=a;
     */
    private TargetField prev;

    public TargetField(TargetField prev, String name) {
        this.prev = prev;
        this.fieldName = name;
    }
    
    public TargetField(TargetField prev, Field field) {
        this.field = field;
        this.owner = field.getDeclaringClass();
        this.fieldName = field.getName();
        this.isStatic = (field.getModifiers() & Modifier.STATIC) > 0;
        this.isPrivate = (field.getModifiers() & Modifier.PRIVATE) > 0;
        this.prev = prev;
    }

    public Field getField() {
        return field;
    }

    public Class<?> getOwner() {
        return owner;
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public TargetField getPrev() {
        return prev;
    }
}
