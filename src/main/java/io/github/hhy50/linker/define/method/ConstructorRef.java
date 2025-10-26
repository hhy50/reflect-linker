package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.generate.MethodHandle;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;


/**
 * The type Constructor ref.
 */
public class ConstructorRef extends MethodRef {

    private final Constructor<?> constructor;

    /**
     * Instantiates a new Constructor ref.
     *
     * @param name        the name
     * @param constructor the constructor
     */
    public ConstructorRef(String name, Constructor<?> constructor) {
        super(name, name);
        this.constructor = constructor;
    }

    @Override
    public MethodHandle defineInvoker() {
        return new io.github.hhy50.linker.generate.constructor.Constructor(this);
    }

    @Override
    public Type getMhType() {
        return null;
    }

    /**
     * Gets declare type.
     *
     * @return the declare type
     */
    public Type getDeclareType() {
        return Type.getType(constructor.getDeclaringClass());
    }
}
