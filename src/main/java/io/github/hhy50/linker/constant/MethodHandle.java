package io.github.hhy50.linker.constant;


import org.objectweb.asm.Type;

/**
 * The type Method handle.
 */
public class MethodHandle {
    /**
     * The constant DESCRIPTOR.
     */
    public static final String DESCRIPTOR = "Ljava/lang/invoke/MethodHandle;";
    /**
     * The constant TYPE.
     */
    public static final Type TYPE = Type.getType(DESCRIPTOR);
}
