package io.github.hhy50.linker.generate.bytecode.vars;

import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.Type;


/**
 * The type Lookup var.
 */
public class LookupVar extends VarInst {

    /**
     * The constant DESCRIPTOR.
     */
    public static final String DESCRIPTOR = "Ljava/lang/invoke/MethodHandles$Lookup;";

    /**
     * The constant TYPE.
     */
    public static final Type TYPE = Type.getType(DESCRIPTOR);

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
