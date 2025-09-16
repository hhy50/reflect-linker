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

    /**
     * 是否静态字段
     */
    private boolean isStatic;
    private Class<?> assignedType;

    /**
     * Instantiates a new Early field ref.
     *
     * @param fullName the fullName
     * @param field    the field
     */
    public EarlyFieldRef(String fullName, Field field) {
        super(fullName, field.getName(), field.getType());
        this.lookup = field.getDeclaringClass();
        this.isStatic = Modifier.isStatic(field.getModifiers());
    }

    /**
     * Instantiates a new Early field ref.
     *
     * @param fullName the fullName
     * @param type     the type
     */
    public EarlyFieldRef(String fullName, Class<?> type) {
        super(fullName, fullName, type);
        this.lookup = type;
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
        if (this.type.isPrimitive() || Modifier.isPublic(this.type.getModifiers())) {
            return Type.getType(this.type);
        }
        return ObjectVar.TYPE;
    }

    /**
     * @param assignedType
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
        return this.type;
    }
}
