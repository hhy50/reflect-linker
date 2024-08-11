package io.github.hhy.linker.bytecode.vars;

public class ObjectVar extends VarInst {

    public ObjectVar(int lvbIndex) {
        super(lvbIndex, "Ljava/lang/Object;");
    }

    public ObjectVar(int lvbIndex, String type) {
        super(lvbIndex, type);
    }
}
