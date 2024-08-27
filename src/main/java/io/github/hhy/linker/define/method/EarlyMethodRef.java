package io.github.hhy.linker.define.method;


import io.github.hhy.linker.define.field.FieldRef;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class EarlyMethodRef extends MethodRef {
    private Method method;
    private final Type methodType;

    public EarlyMethodRef(FieldRef owner, Method method) {
        super(owner, method.getName());
        this.method = method;
        this.methodType = Type.getType(method);
    }

    public boolean isStatic() {
        return Modifier.isStatic(method.getModifiers());
    }

    public Type getMethodType() {
        return this.methodType;
    }
}
