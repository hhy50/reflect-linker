package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.invoker.Setter;
import org.objectweb.asm.Type;

/**
 * The type Field setter method ref.
 */
public class FieldSetterMethodRef extends MethodRef {
    private final FieldRef field;

    /**
     * Instantiates a new Field setter method ref.
     *
     * @param field the field
     */
    public FieldSetterMethodRef(FieldRef field) {
        super(field.getName());
        this.field = field;
        this.setNullable(field.isNullable());
    }

    @Override
    public Type getMethodType() {
        return Type.getMethodType(Type.VOID_TYPE, field.getType());
    }

    @Override
    public MethodHandle defineInvoker() {
        if (field instanceof RuntimeFieldRef) {
            return new Setter.WithRuntime(field);
        }
        return new Setter.WithEarly((EarlyFieldRef) field);
    }
}