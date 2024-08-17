package io.github.hhy.linker.define.field;


import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 前期就确定好类型的字段，包括：
 * 1. target字段
 * 2. 能在上级class中找到的
 * 3. 使用@Typed注解的字段
 */
public class EarlyFieldRef extends FieldRef {

    private Field field;

    public Type type;

    public EarlyFieldRef(FieldRef prev, Field field, Type realType) {
        super(prev, field.getName());
        this.field = field;
        this.type = realType == null ? Type.getType(field.getType()) : realType;
    }

    public EarlyFieldRef(FieldRef prev, String fieldName, Type realType) {
        super(prev, fieldName);
        this.type = realType;
    }

    public boolean isStatic() {
        return Modifier.isStatic(this.field.getModifiers());
    }

    @Override
    public Type getType() {
        return this.type;
    }
}
