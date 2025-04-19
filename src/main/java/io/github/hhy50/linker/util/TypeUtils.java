package io.github.hhy50.linker.util;

import org.objectweb.asm.Type;

import java.util.Arrays;

public class TypeUtils {

    /**
     * The MethodHandle TYPE.
     */
    public static final Type METHOD_HANDLER_TYPE = Type.getType("Ljava/lang/invoke/MethodHandle;");

    public static Type getMethodType(Class<?> rType, Class<?>... argsType) {
        return Type.getMethodType(Type.getType(rType), Arrays.stream(argsType)
                .map(Type::getType).toArray(Type[]::new));
    }
}
