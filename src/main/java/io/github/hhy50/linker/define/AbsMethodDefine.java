package io.github.hhy50.linker.define;


import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.method.MethodRef;

import java.lang.reflect.Method;

/**
 * The type Method define.
 */
public class AbsMethodDefine {

    /**
     * The Method.
     */
    public Method method;

    /**
     * The Field ref.
     */
    public FieldRef fieldRef;

    /**
     * The Method ref.
     */
    public MethodRef methodRef;

    /**
     * Instantiates a new Method define.
     *
     * @param method the method
     */
    public AbsMethodDefine(Method method) {
        this.method = method;
    }

    /**
     * Has setter boolean.
     *
     * @return the boolean
     */
    public boolean hasSetter() {
        return method.getDeclaredAnnotation(Field.Setter.class) != null;
    }

    /**
     * Has getter boolean.
     *
     * @return the boolean
     */
    public boolean hasGetter() {
        return method.getDeclaredAnnotation(Field.Getter.class) != null;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        if (fieldRef != null) {
            return fieldRef.getUniqueName();
        }
        return method.getName();
    }

    /**
     * Has constructor boolean.
     *
     * @return the boolean
     */
    public boolean hasConstructor() {
        return method.getDeclaredAnnotation(io.github.hhy50.linker.annotations.Method.Constructor.class) != null;
    }
}
