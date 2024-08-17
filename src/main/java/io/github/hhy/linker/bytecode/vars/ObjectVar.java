package io.github.hhy.linker.bytecode.vars;

import org.objectweb.asm.Type;

public class ObjectVar extends VarInst {

    public static final Type TYPE = Type.getType("Ljava/lang/Object;");

    public ObjectVar(int lvbIndex) {
        super(lvbIndex, TYPE);
    }

    public ObjectVar(int lvbIndex, Type type) {
        super(lvbIndex, type);
    }
}
