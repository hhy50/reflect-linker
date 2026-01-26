package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.invoker.Setter;
import org.objectweb.asm.Type;

public class FieldSetterMethodRef extends MethodRef {
    private final FieldRef field;

    public FieldSetterMethodRef(FieldRef field) {
        super(field.getFullName());
        this.field = field;
    }

    @Override
    public Type getLookupType() {
        return Type.getMethodType(Type.VOID_TYPE, field.getType());
    }

    @Override
    public String getFullName() {
        return "setter:"+field.getFullName();
    }

    @Override
    public MethodHandle defineInvoker() {
        return new Setter(field);
    }
}
