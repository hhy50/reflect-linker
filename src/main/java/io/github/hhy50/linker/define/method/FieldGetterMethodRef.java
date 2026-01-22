package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.invoker.Getter;
import org.objectweb.asm.Type;

public class FieldGetterMethodRef extends MethodRef {
    private final FieldRef field;

    public FieldGetterMethodRef(FieldRef field) {
        super(field.getName());
        this.field = field;
    }

    @Override
    public Type getLookupMhType() {
        return Type.getMethodType(field.getType());
    }

    @Override
    public MethodHandle defineInvoker() {
        return new Getter(field);
    }

    @Override
    public String getFullName() {
        return "getter:"+field.getFullName();
    }

    @Override
    public boolean isRuntime() {
        return field.getType() == ObjectVar.TYPE;
    }
}
