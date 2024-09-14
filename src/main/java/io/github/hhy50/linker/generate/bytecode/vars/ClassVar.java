package io.github.hhy50.linker.generate.bytecode.vars;

import org.objectweb.asm.Type;

public class ClassVar extends VarInst{

    public static final Type TYPE = Type.getType(Class.class);

    /**
     * <p>Constructor for VarInst.</p>
     *
     * @param lvbIndex a int.
     */
    public ClassVar(int lvbIndex) {
        super(lvbIndex, TYPE);
    }
}
