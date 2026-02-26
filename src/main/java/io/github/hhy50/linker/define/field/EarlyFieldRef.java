package io.github.hhy50.linker.define.field;


import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * The type Early field ref.
 */
public class EarlyFieldRef extends FieldRef {

    private Class<?> lookupClass;

    private Class<?> fieldType;

    /**
     * 是否静态字段
     */
    private boolean isStatic;

    /**
     * assignedType
     */
    private Class<?> assignedType;

    /**
     * Instantiates a new Early field ref.
     *
     * @param fullName the fullName
     * @param field    the field
     */
    public EarlyFieldRef(String fullName, Field field) {
        super(fullName, field.getName());
        this.lookupClass = field.getDeclaringClass();
        this.fieldType = field.getType();
        this.isStatic = Modifier.isStatic(field.getModifiers());
    }

    /**
     * Instantiates a new Early field ref.
     *
     * @param fullName  the fullName
     * @param fieldType the fieldType
     */
    public EarlyFieldRef(String fullName, Class<?> fieldType) {
        super(fullName, fullName);
        this.fieldType = fieldType;
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
     * Gets lookup class.
     *
     * @return the lookup class
     */
    public Type getLookupClass() {
        return Type.getType(this.lookupClass);
    }
}
