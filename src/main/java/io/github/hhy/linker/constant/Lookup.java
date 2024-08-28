package io.github.hhy.linker.constant;

import org.objectweb.asm.Type;

public class Lookup {
    public static final String OWNER = "java/lang/invoke/MethodHandles$Lookup";
    public static final String DESCRIPTOR = "Ljava/lang/invoke/MethodHandles$Lookup;";
    public static final Type TYPE = Type.getType(DESCRIPTOR);
    public static final String FIND_XETTER_DESC = "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)"+MethodHandle.DESCRIPTOR;
}
