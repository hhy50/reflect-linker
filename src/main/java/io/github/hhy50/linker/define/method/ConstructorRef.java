package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.define.field.EarlyFieldRef;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.util.Arrays;


/**
 * The type Constructor ref.
 */
public class ConstructorRef extends MethodRef {

    private final Constructor<?> constructor;


    /**
     * Instantiates a new Constructor ref.
     *
     * @param owner       the owner
     * @param name        the name
     * @param constructor the constructor
     */
    public ConstructorRef(EarlyFieldRef owner, String name, Constructor<?> constructor) {
        super(owner, name);
        this.constructor = constructor;
    }

    @Override
    public Type[] getArgsType() {
        return Arrays.stream(this.constructor.getParameterTypes()).map(Type::getType)
                .toArray(Type[]::new);
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
