package io.github.hhy50.linker.define.field;


import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 前期就确定好类型的字段，包括：
 * 1. target字段
 * 2. 能在上级class中找到的
 * 3. 使用@Typed注解的字段
 *
 * @author hanhaiyang
 * @version $Id: $Id
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

    /**
     * <p>Constructor for EarlyFieldRef.</p>
     *
     * @param prev a {@link io.github.hhy50.linker.define.field.FieldRef} object.
     * @param field a {@link java.lang.reflect.Field} object.
     * @param assignedType a {@link java.lang.Class} object.
     */
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
     * @param prev a {@link io.github.hhy50.linker.define.field.FieldRef} object.
     * @param objName a {@link java.lang.String} object.
     * @param fieldName a {@link java.lang.String} object.
     * @param fieldTypeClass a {@link java.lang.Class} object.
     */
    public EarlyFieldRef(FieldRef prev, String objName, String fieldName, Class<?> fieldTypeClass) {
        super(prev, objName, fieldName);
        this.fieldType = Type.getType(fieldTypeClass);
        this.fieldRealTypeClass = fieldTypeClass;
    }

    /**
     * <p>isStatic.</p>
     *
     * @return a boolean.
     */
    public boolean isStatic() {
        return this.isStatic;
    }

    /** {@inheritDoc} */
    @Override
    public Type getType() {
        return this.fieldType;
    }

    /**
     * <p>Getter for the field <code>declaredType</code>.</p>
     *
     * @return a {@link java.lang.Class} object.
     */
    public Class<?> getDeclaredType() {
        return this.declaredType;
    }

    /**
     * <p>getClassType.</p>
     *
     * @return a {@link java.lang.Class} object.
     */
    public Class<?> getClassType() {
        return this.fieldRealTypeClass;
    }
}
