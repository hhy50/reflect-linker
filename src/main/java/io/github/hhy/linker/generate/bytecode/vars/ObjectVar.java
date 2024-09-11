package io.github.hhy.linker.generate.bytecode.vars;

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
    public ObjectVar(int lvbIndex) {
        super(lvbIndex, TYPE);
    }

    /**
     * <p>Constructor for ObjectVar.</p>
     *
     * @param lvbIndex a int.
     * @param type a {@link org.objectweb.asm.Type} object.
     */
    public ObjectVar(int lvbIndex, Type type) {
        super(lvbIndex, type);
    }
}
