package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.invoker.Getter;
import org.objectweb.asm.Type;

/**
 * The type Field getter method ref.
 */
public class FieldGetterMethodRef extends MethodRef {
    private final FieldRef field;

    /**
     * Instantiates a new Field getter method ref.
     *
     * @param field the field
     */
    public FieldGetterMethodRef(FieldRef field) {
        super(field.getName());
        this.field = field;
        this.setIndexs(field.getIndexs());
    }

    @Override
    public Type getLookupType() {
        return Type.getMethodType(field.getType());
    }

    @Override
    public MethodHandle defineInvoker() {
        if (field instanceof RuntimeFieldRef) {
            return new Getter.WithRuntime(field);
        } else if (field instanceof EarlyFieldRef) {
            return new Getter.WithEarly((EarlyFieldRef) field);
        }
        return null;
    }

    @Override
    public String getFullName() {
        return "getter:"+field.getFullName();
    }
}
