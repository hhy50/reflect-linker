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
    private Class<?> declaredType;

    /**
     * 字段类型
     */
    private final Type fieldType;
    private final Class<?> fieldRealTypeClass;

    /**
     * 是否是静态字段
     */
    private boolean isStatic;

    public EarlyFieldRef(FieldRef prev, Field field, Class<?> assignedType) {
        super(prev, prev.getUniqueName(), field.getName());
        this.declaredType = field.getDeclaringClass();
        this.fieldRealTypeClass = assignedType == null ? field.getType() : assignedType;
        this.fieldType = Type.getType(field.getType());
        this.isStatic = Modifier.isStatic(field.getModifiers());
    }

    /**
     * 仅能表示target
     *
     * @param prev
     * @param objName
     * @param fieldName
     * @param fieldTypeClass
     */
    public EarlyFieldRef(FieldRef prev, String objName, String fieldName, Class<?> fieldTypeClass) {
        super(prev, objName, fieldName);
        this.fieldType = Type.getType(fieldTypeClass);
        this.fieldRealTypeClass = fieldTypeClass;
    }

    public boolean isStatic() {
        return this.isStatic;
    }

    @Override
    public Type getType() {
        return this.fieldType;
    }

    public Class<?> getDeclaredType() {
        return this.declaredType;
    }

    public Class<?> getClassType() {
        return this.fieldRealTypeClass;
    }
}
