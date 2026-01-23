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
        super(name);
        this.constructor = constructor;
    }

    @Override
    public MethodHandle defineInvoker() {
        return new io.github.hhy50.linker.generate.constructor.Constructor(this);
    }

    /**
     * Gets declare type.
     *
     * @return the declare type
     */
    public Type getLookupClass() {
        return Type.getType(constructor.getDeclaringClass());
    }

    @Override
    public Type getLookupType() {
        return Type.getType(constructor);
    }

    /**
     *
     * @return
     */
    public String getFullName() {
        return "constructor";
    }

    @Override
    public boolean isRuntime() {
        return false;
    }


}
