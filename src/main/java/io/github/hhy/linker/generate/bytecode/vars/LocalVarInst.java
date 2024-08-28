package io.github.hhy.linker.generate.bytecode.vars;

import org.objectweb.asm.Type;

public class LocalVarInst extends VarInst {

    private final String varName;

    public LocalVarInst(int lvbIndex, Type type, String varName) {
        super(lvbIndex, type);
        this.varName = varName == null ? "var" + lvbIndex : varName;
    }

    @Override
    public String getName() {
        return varName+"[type="+type.getClassName()+"]";
    }
}
