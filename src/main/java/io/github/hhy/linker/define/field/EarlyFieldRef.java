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

    /**
     * objDeclaredType
     * 声明的类的类型
     */
    private Type declaredType;

    /**
     * objRealType
     * 指定的类型
     */
//    public Type objRealType;

    /**
     * 字段类型
     */
    private final Class<?> fieldTypeClass;
    private final Type fieldType;


    /**
     * 是否是静态字段
     */
    private boolean isStatic;

    public EarlyFieldRef(FieldRef prev, Field field) {
        super(prev, prev.getUniqueName(), field.getName());
        this.declaredType = Type.getType(field.getDeclaringClass());
        this.fieldTypeClass = field.getType();
        this.fieldType = Type.getType(this.fieldTypeClass);
        this.isStatic = Modifier.isStatic(field.getModifiers());
    }

    public EarlyFieldRef(FieldRef prev, Field field, Class<?> objRealType) {
        super(prev, prev.getUniqueName(), field.getName());
        this.declaredType = Type.getType(field.getDeclaringClass());
        this.fieldTypeClass = objRealType;
        this.fieldType = Type.getType(field.getType());
        this.isStatic = Modifier.isStatic(field.getModifiers());
    }

    /**
     * 仅能表示target
     *
     * @param prev
     * @param objName
     * @param fieldName
     * @param realType
     */
    public EarlyFieldRef(FieldRef prev, String objName, String fieldName, Class<?> fieldTypeClass) {
        super(prev, objName, fieldName);
        this.fieldTypeClass = fieldTypeClass;
        this.fieldType = Type.getType(fieldTypeClass);
    }

    public boolean isStatic() {
        return this.isStatic;
    }

    @Override
    public Type getType() {
        return this.fieldType;
    }

    public Type getDeclaredType() {
        return this.declaredType;
    }

    public Class<?> getFieldTypeClass() {
        return this.fieldTypeClass;
    }

    public String getFieldTypeName() {
        return this.fieldTypeClass.getName();
    }
}
