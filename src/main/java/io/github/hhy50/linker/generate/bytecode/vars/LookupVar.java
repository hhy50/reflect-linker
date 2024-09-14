package io.github.hhy50.linker.generate.bytecode.vars;

import io.github.hhy50.linker.constant.MethodHandle;
import org.objectweb.asm.Type;


public class LookupVar extends VarInst {
    public static final String OWNER = "java/lang/invoke/MethodHandles$Lookup";
    public static final String DESCRIPTOR = "Ljava/lang/invoke/MethodHandles$Lookup;";

    public static final Type TYPE = Type.getType(DESCRIPTOR);
    public static final String FIND_XETTER_DESC = "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)"+ MethodHandle.DESCRIPTOR;
    public static final String FIND_VIRTUAL = "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)"+MethodHandle.DESCRIPTOR;
    public static final String FIND_SPECIAL = "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/Class;)"+MethodHandle.DESCRIPTOR;

    /**
     * <p>Constructor for VarInst.</p>
     *
     * @param lvbIndex a int.
     * @param type     a {@link Type} object.
     */
    public LookupVar(int lvbIndex, Type type) {
        super(lvbIndex, type);
    }
}
