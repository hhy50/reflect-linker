package io.github.hhy50.linker.generate.bytecode.vars;


import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import org.objectweb.asm.Type;

/**
 * The type Array var inst.
 */
public class ArrayVarInst extends VarInst {

    private final VarInst varInst;

    /**
     * Instantiates a new Array var inst.
     *
     * @param varInst the var inst
     */
    public ArrayVarInst(VarInst varInst) {
        this.varInst = varInst;
    }

    /**
     * Index var inst.
     *
     * @param i the
     * @return the var inst
     */
    public VarInst index(int i) {
        return VarInst.wrap(Actions.arrayIndex(this, i), varInst.getType().getElementType());
    }

    @Override
    public Action load() {
        return varInst;
    }

    @Override
    public Type getType() {
        return varInst.getType();
    }
}
