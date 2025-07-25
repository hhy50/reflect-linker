package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.invoker.Invoker;
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
    public ConstructorRef(EarlyFieldRef owner, String name, Constructor<?> constructor) {
        super(owner, name);
        this.constructor = constructor;
    }

    public Type getMethodType() {
        return Type.getMethodType(ObjectVar.TYPE, Type.getType(constructor).getArgumentTypes());
    }

    @Override
    public Invoker<?> defineInvoker() {
        return new io.github.hhy50.linker.generate.constructor.Constructor(this);
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
