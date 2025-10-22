package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.generate.invoker.Invoker;
import org.objectweb.asm.Type;

public class FieldGetterMethodRef extends MethodRef {
    private final FieldRef field;

    public FieldGetterMethodRef(FieldRef field) {
        super("get_" + field.getFullName(), "get_" + field.getFullName());
        this.field = field;
    }

    @Override
    public Type getMethodType() {
        return Type.getMethodType(field.getType());
    }

    @Override
    public Invoker<?> defineInvoker() {
        throw new UnsupportedOperationException("Unimplemented method 'defineInvoker'");
    }
}
