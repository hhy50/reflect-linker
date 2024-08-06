package io.github.hhy.linker.define;

import java.lang.reflect.Modifier;

/**
 * 编译时字段, 通过反射获取到
 */
public class CompileField extends Field {
    /**
     *
     */
    private java.lang.reflect.Field field;

    /**
     * 声明类
     */
    private Class<?> declare;

    /**
     * 是否是静态字段
     */
    private boolean isStatic;

    /**
     * 是否是私有字段
     */
    private boolean isPrivate;


    public CompileField(Field prev, java.lang.reflect.Field field) {
        super(prev, field.getName());
        this.field = field;
        this.declare = field.getDeclaringClass();
        this.isStatic = (field.getModifiers() & Modifier.STATIC) > 0;
        this.isPrivate = (field.getModifiers() & Modifier.PRIVATE) > 0;
    }
}
