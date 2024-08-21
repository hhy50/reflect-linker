package io.github.hhy.linker.define.field2;


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

    /**
     * objDeclaredType
     * 生明的类型
     */
    public Type declaredType;

    /**
     * objRealType
     * 指定的类型
     */
    public Type realType;

    /**
     * 字段类型
     */
    public Type fieldType;

    /**
     * 是否是静态字段
     */
    public boolean isStatic;

    public EarlyFieldRef(FieldRef prev, Field field) {
        super(prev, prev.getFullName(), field.getName());
        this.declaredType = Type.getType(field.getDeclaringClass());
        this.realType = this.declaredType;
        this.fieldType = Type.getType(field.getType());
        this.isStatic = Modifier.isStatic(field.getModifiers());
    }

    public EarlyFieldRef(FieldRef prev, Field field, Type objRealType) {
        super(prev, prev.getFullName(), field.getName());
        this.declaredType = Type.getType(field.getDeclaringClass());
        this.realType = objRealType;
        this.fieldType = Type.getType(field.getType());
        this.isStatic = Modifier.isStatic(field.getModifiers());
    }

    /**
     * 仅能表示target
     * @param prev
     * @param objName
     * @param fieldName
     * @param realType
     */
    public EarlyFieldRef(FieldRef prev, String objName, String fieldName, Type realType) {
        super(prev, objName, fieldName);
        this.fieldType = realType;
    }

    public boolean isStatic() {
        return this.isStatic;
    }

    @Override
    public Type getType() {
        return this.fieldType;
    }
}
