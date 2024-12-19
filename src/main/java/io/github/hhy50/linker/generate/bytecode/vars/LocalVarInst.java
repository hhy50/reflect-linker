package io.github.hhy50.linker.generate.bytecode.vars;

import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.Type;

/**
 * The type Local var inst.
 */
public class LocalVarInst extends VarInst {

    private final String varName;

    /**
     * Instantiates a new Local var inst.
     *
     * @param body     the body
     * @param lvbIndex the lvb index
     * @param type     the type
     * @param varName  the var name
     */
    public LocalVarInst(MethodBody body, int lvbIndex, Type type, String varName) {
        super(body, lvbIndex, type);
        this.varName = varName == null ? "var" + lvbIndex : varName;
    }

    @Override
    public String getName() {
        return varName + "[type=" + type.getClassName() + "]";
    }
}
