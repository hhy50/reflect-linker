package io.github.hhy50.linker.util;

import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandle;
import java.util.Arrays;

/**
 * The type Type utils.
 */
public class TypeUtils {

    /**
     * The constant METHOD_HANDLER_TYPE.
     */
    public static final Type METHOD_HANDLER_TYPE = Type.getType(MethodHandle.class);
    /**
     * The constant STRING_TYPE.
     */
    public static final Type STRING_TYPE = Type.getType(String.class);
    /**
     * The constant CLASS_TYPE.
     */
    public static final Type CLASS_TYPE = Type.getType(Class.class);

    /**
     * Gets method type.
     *
     * @param rType    the r type
     * @param argsType the args type
     * @return the method type
     */
    public static Type getMethodType(Class<?> rType, Class<?>... argsType) {
        return Type.getMethodType(Type.getType(rType), Arrays.stream(argsType)
                .map(Type::getType).toArray(Type[]::new));
    }
}
