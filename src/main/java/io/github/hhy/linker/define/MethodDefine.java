package io.github.hhy.linker.define;


import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.define.field.FieldRef;

import java.lang.reflect.Method;

public class MethodDefine {

    public Method define;

    public FieldRef fieldRef;

    public MethodDefine(Method method) {
        this.define = method;
    }

    public boolean hasSetter() {
        return define.getDeclaredAnnotation(Field.Setter.class) != null;
    }

    public boolean hasGetter() {
        return define.getDeclaredAnnotation(Field.Getter.class) != null;
    }

    public String getName() {
        if (fieldRef != null) {
            return fieldRef.getFullName();
        }
        return define.getName();
    }
}
