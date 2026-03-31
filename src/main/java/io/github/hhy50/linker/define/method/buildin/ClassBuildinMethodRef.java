package io.github.hhy50.linker.define.method.buildin;

import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.invoker.DirectMethodInvoker;
import org.objectweb.asm.Type;

/**
 * The type Method ref.
 */
public class ClassBuildinMethodRef extends MethodRef {
    /**
     * Instantiates a new Method ref.
     *
     */
    public ClassBuildinMethodRef() {
        super("class");
    }

    @Override
    public MethodHandle defineInvoker() {
        try {
            return new DirectMethodInvoker(Class.class.getDeclaredMethod("forName", String.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Type getMethodType() {
        return Type.getMethodType(Type.getType(Class.class), Type.getType(String.class));
    }
}
