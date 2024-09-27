package io.github.hhy50.linker.generate.bytecode.vars;

import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.Type;

/**
 * The type Class var.
 */
public class ClassVar extends VarInst{

    /**
     * The constant TYPE.
     */
    public static final Type TYPE = Type.getType(Class.class);

    /**
     * Instantiates a new Class var.
     *
     * @param body     the body
     * @param lvbIndex the lvb index
     */
    public ClassVar(MethodBody body, int lvbIndex) {
        super(body, lvbIndex, TYPE);
    }
}
