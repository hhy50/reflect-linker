package io.github.hhy50.linker.define;


import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.method.MethodRef;

import java.lang.reflect.Method;

/**
 * <p>MethodDefine class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class MethodDefine {

    public Method define;

    public FieldRef fieldRef;

    public MethodRef methodRef;

    /**
     * <p>Constructor for MethodDefine.</p>
     *
     * @param method a {@link java.lang.reflect.Method} object.
     */
    public MethodDefine(Method method) {
        this.define = method;
    }

    /**
     * <p>hasSetter.</p>
     *
     * @return a boolean.
     */
    public boolean hasSetter() {
        return define.getDeclaredAnnotation(Field.Setter.class) != null;
    }

    /**
     * <p>hasGetter.</p>
     *
     * @return a boolean.
     */
    public boolean hasGetter() {
        return define.getDeclaredAnnotation(Field.Getter.class) != null;
    }

    /**
     * <p>getName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        if (fieldRef != null) {
            return fieldRef.getUniqueName();
        }
        return define.getName();
    }
}
