package io.github.hhy50.linker.generate.bytecode.vars;


import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.Type;

/**
 * The type Var inst.
 */
public abstract class InvalidVarInst extends VarInst {

    /**
     * Instantiates a new Var inst.
     *
     * @param lvbIndex the lvb index
     */
    public InvalidVarInst(int lvbIndex) {
        super(null, lvbIndex, null);
    }

    @Override
    public void load(MethodBody methodBody) {
        throw new RuntimeException("the var"+this.lvbIndex + " unavailable");
    }
}
