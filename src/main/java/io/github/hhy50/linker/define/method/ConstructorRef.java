package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;


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

    public Type getMhType() {
        Type rType = ObjectVar.TYPE;
        if (Modifier.isPublic(constructor.getDeclaringClass().getModifiers())) {
            rType = Type.getType(constructor.getDeclaringClass());
        }
        return Type.getMethodType(rType, Type.getType(constructor).getArgumentTypes());
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
    public Type getDeclareType() {
        return Type.getType(constructor.getDeclaringClass());
    }
}
