package io.github.hhy50.linker.generate.bytecode.vars;

import io.github.hhy50.linker.constant.MethodHandle;
import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.Type;


/**
 * The type Lookup var.
 */
public class LookupVar extends VarInst {
    /**
     * The constant OWNER.
     */
    public static final String OWNER = "java/lang/invoke/MethodHandles$Lookup";
    /**
     * The constant DESCRIPTOR.
     */
    public static final String DESCRIPTOR = "Ljava/lang/invoke/MethodHandles$Lookup;";

    /**
     * The constant TYPE.
     */
    public static final Type TYPE = Type.getType(DESCRIPTOR);
    /**
     * The constant FIND_XETTER_DESC.
     */
    public static final String FIND_XETTER_DESC = "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)"+ MethodHandle.DESCRIPTOR;
    /**
     * The constant FIND_VIRTUAL.
     */
    public static final String FIND_VIRTUAL = "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)"+MethodHandle.DESCRIPTOR;
    /**
     * The constant FIND_SPECIAL.
     */
    public static final String FIND_SPECIAL = "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/Class;)"+MethodHandle.DESCRIPTOR;

    /**
     * Instantiates a new Lookup var.
     *
     * @param body     the body
     * @param lvbIndex the lvb index
     * @param type     the type
     */
    public LookupVar(MethodBody body, int lvbIndex, Type type) {
        super(body, lvbIndex, type);
    }
}
