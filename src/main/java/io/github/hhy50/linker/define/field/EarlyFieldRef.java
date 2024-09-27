package io.github.hhy50.linker.define.field;


import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * The type Early field ref.
 */
public class EarlyFieldRef extends FieldRef {

    private Class<?> declaredType;

    private final Type fieldType;
    private final Class<?> fieldRealTypeClass;

    private boolean isStatic;

    /**
     * Instantiates a new Early field ref.
     *
     * @param prev         the prev
     * @param field        the field
     * @param assignedType the assigned type
     */
    public EarlyFieldRef(FieldRef prev, Field field, Class<?> assignedType) {
        super(prev, prev.getUniqueName(), field.getName());
        this.declaredType = field.getDeclaringClass();
        this.fieldRealTypeClass = assignedType == null ? field.getType() : assignedType;
        this.fieldType = Type.getType(field.getType());
        this.isStatic = Modifier.isStatic(field.getModifiers());
    }

    /**
     * Instantiates a new Early field ref.
     *
     * @param prev           the prev
     * @param objName        the obj name
     * @param fieldName      the field name
     * @param fieldTypeClass the field type class
     */
    public EarlyFieldRef(FieldRef prev, String objName, String fieldName, Class<?> fieldTypeClass) {
        super(prev, objName, fieldName);
        this.fieldType = Type.getType(fieldTypeClass);
        this.fieldRealTypeClass = fieldTypeClass;
    }

    /**
     * Is static boolean.
     *
     * @return the boolean
     */
    public boolean isStatic() {
        return this.isStatic;
    }

    @Override
    public Type getType() {
        return this.fieldType;
    }

    /**
     * Gets declared type.
     *
     * @return the declared type
     */
    public Type getDeclaredType() {
        return Type.getType(this.declaredType);
    }

    /**
     * Gets class type.
     *
     * @return the class type
     */
    public Class<?> getClassType() {
        return this.fieldRealTypeClass;
    }
}
