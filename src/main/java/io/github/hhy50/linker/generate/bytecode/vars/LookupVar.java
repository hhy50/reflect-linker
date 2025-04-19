package io.github.hhy50.linker.generate.bytecode.vars;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.util.TypeUtils;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;


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
    public static final Type FIND_XETTER_DESC = TypeUtils.getMethodType(MethodHandle.class, Class.class, String.class, Class.class);
    /**
     * The constant FIND_VIRTUAL.
     */
    public static final Type FIND_XXXXX = TypeUtils.getMethodType(MethodHandle.class, Class.class, String.class, MethodType.class);
    /**
     * The constant FIND_SPECIAL.
     */
    public static final Type FIND_SPECIAL = TypeUtils.getMethodType(MethodHandle.class, Class.class, String.class, MethodType.class, Class.class);
    /**
     * The constant FIND_CONSTRUCTOR.
     */
    public static final Type FIND_CONSTRUCTOR = TypeUtils.getMethodType(MethodHandle.class, Class.class, MethodType.class);

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
