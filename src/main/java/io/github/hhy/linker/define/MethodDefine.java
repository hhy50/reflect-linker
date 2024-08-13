package io.github.hhy.linker.define;


import io.github.hhy.linker.annotations.Field;

import java.lang.reflect.Method;

public class MethodDefine {
    public Method define;
    public TargetPoint targetPoint;

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
        if (targetPoint instanceof RuntimeField) {
            return ((RuntimeField) targetPoint).getFullName();
        }
        return define.getName();
    }
}
