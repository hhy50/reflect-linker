package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.invoker.Getter;
import org.objectweb.asm.Type;

public class FieldGetterMethodRef extends MethodRef {
    private final FieldRef field;

    public FieldGetterMethodRef(FieldRef field) {
        super(field.getFullName(), field.getName());
        this.field = field;
    }

    @Override
    public Type getMhType() {
        return Type.getMethodType(field.getType());
    }

    @Override
    public MethodHandle defineInvoker() {
        return new Getter(field);
    }
}
