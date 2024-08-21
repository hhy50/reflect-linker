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

    public Type objDeclaredType;

    public Type objRealType;

    public Type fieldType;

    public boolean isStatic;

    public EarlyFieldRef(FieldRef prev, String objName, Field field) {
        super(prev, objName, field.getName());
        this.objDeclaredType = Type.getType(field.getDeclaringClass());
        this.objRealType = this.objDeclaredType;
        this.fieldType = Type.getType(field.getType());
        this.isStatic = Modifier.isStatic(field.getModifiers());
    }

    public EarlyFieldRef(FieldRef prev, String objName, Field field, Type objRealType) {
        super(prev, objName, field.getName());
        this.objDeclaredType = Type.getType(field.getDeclaringClass());
        this.objRealType = objRealType;
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
