package io.github.hhy50.linker.define.field;


import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * The type Early field ref.
 */
public class EarlyFieldRef extends FieldRef {

    private Class<?> lookup;
    private final Class<?> declaredType;
    private Class<?> assignedType;
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
        this.lookup = field.getDeclaringClass();
        this.declaredType = field.getType();
        this.assignedType = assignedType;
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
        this.declaredType = fieldTypeClass;
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
        if (this.declaredType.isPrimitive() || Modifier.isPublic(this.declaredType.getModifiers())) {
            return Type.getType(this.declaredType);
        }
        return ObjectVar.TYPE;
    }

    /**
     * Gets lookup class.
     *
     * @return the lookup class
     */
    public Type getLookupClass() {
        return Type.getType(this.lookup);
    }

    /**
     * Gets decalared type.
     *
     * @return the decalared type
     */
    public Type getDecalaredType() {
        return Type.getType(this.declaredType);
    }

    /**
     * Gets class type.
     *
     * @return the class type
     */
    public Class<?> getClassType() {
        if (this.assignedType != null) {
            return this.assignedType;
        }
        return this.declaredType;
    }
}
