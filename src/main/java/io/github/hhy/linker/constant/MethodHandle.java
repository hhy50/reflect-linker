package io.github.hhy.linker.constant;


import org.objectweb.asm.Type;

public class MethodHandle {
    public static final String DESCRIPTOR = "Ljava/lang/invoke/MethodHandle;";
    public static final Type TYPE = Type.getType(DESCRIPTOR);
}
