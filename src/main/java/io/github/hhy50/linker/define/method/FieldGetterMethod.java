package io.github.hhy50.linker.define.method;

import org.objectweb.asm.Type;

import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.generate.invoker.Invoker;

public class FieldGetterMethod extends MethodRef {
    private final FieldRef field;

    public FieldGetterMethod(FieldRef field) {
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
