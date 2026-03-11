package io.github.hhy50.linker.define.field;


import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * The type Early field ref.
 */
public class EarlyFieldRef extends FieldRef {

    private final Field reflect;

    private Class<?> fieldType;

    /**
     * assignedType
     */
    private Class<?> assignedType;

    /**
     * Instantiates a new Early field ref.
     *
     * @param field    the field
     */
    public EarlyFieldRef(Field field) {
        super(field.getName());
        this.reflect = field;
        this.fieldType = field.getType();
    }

    /**
     * Instantiates a new Early field ref.
     *
     * @param fieldName  the fieldName
     * @param fieldType the fieldType
     */
    public EarlyFieldRef(String fieldName, Class<?> fieldType) {
        super(fieldName);
        this.reflect = null;
        this.fieldType = fieldType;
    }

    @Override
    public Type getType() {
        if (this.fieldType.isPrimitive() || Modifier.isPublic(this.fieldType.getModifiers())) {
            return Type.getType(this.fieldType);
        }
        return ObjectVar.TYPE;
    }

    /**
     * Sets assigned type.
     *
     * @param assignedType the assigned type
     */
    public void setAssignedType(Class<?> assignedType) {
        this.assignedType = assignedType;
    }

    /**
     * @return
     */
    public Class<?> getActualType() {
        if (this.assignedType != null) {
            return this.assignedType;
        }
        return this.fieldType;
    }

    /**
     * Gets reflect.
     *
     * @return the reflect
     */
    public Field getReflect() {
        return this.reflect;
    }
}
