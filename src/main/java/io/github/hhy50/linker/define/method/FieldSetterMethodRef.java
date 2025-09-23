package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.generate.invoker.Invoker;
import org.objectweb.asm.Type;

public class FieldSetterMethodRef extends MethodRef {
    private final FieldRef field;

    public FieldSetterMethodRef(FieldRef field) {
        super("set_" + field.getFullName(), "set_" + field.getFullName());
        this.field = field;
    }

    @Override
    public Type getMethodType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMethodType'");
    }

    @Override
    public Invoker<?> defineInvoker() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'defineInvoker'");
    }

}
