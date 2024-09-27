package io.github.hhy50.linker.generate.bytecode.vars;

import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.Type;

/**
 * The type Object var.
 */
public class ObjectVar extends VarInst {

    /**
     * The constant TYPE.
     */
    public static final Type TYPE = Type.getType("Ljava/lang/Object;");

    /**
     * <p>Constructor for ObjectVar.</p>
     *
     * @param lvbIndex a int.
     */

    /**
     * Instantiates a new Object var.
     *
     * @param body     the body
     * @param lvbIndex the lvb index
     * @param type     the type
     */
    public ObjectVar(MethodBody body, int lvbIndex, Type type) {
        super(body, lvbIndex, type);
    }
}
