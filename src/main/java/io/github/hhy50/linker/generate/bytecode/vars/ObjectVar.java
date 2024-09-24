package io.github.hhy50.linker.generate.bytecode.vars;

import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.Type;

/**
 * <p>ObjectVar class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class ObjectVar extends VarInst {

    /** Constant <code>TYPE</code> */
    public static final Type TYPE = Type.getType("Ljava/lang/Object;");

    /**
     * <p>Constructor for ObjectVar.</p>
     *
     * @param lvbIndex a int.
     */

    /**
     * <p>Constructor for ObjectVar.</p>
     *
     * @param body
     * @param lvbIndex   a int.
     * @param type       a {@link Type} object.
     */
    public ObjectVar(MethodBody body, int lvbIndex, Type type) {
        super(body, lvbIndex, type);
    }
}
