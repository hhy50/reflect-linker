package io.github.hhy50.linker.generate.bytecode.vars;

import io.github.hhy50.linker.generate.bytecode.ClassTypeFieldVarInst;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import org.objectweb.asm.Type;

/**
 * The type Var inst with lookup.
 */
public class VarInstWithLookup extends VarInst {

    private final VarInst varInst;
    private final ClassTypeFieldVarInst lookupClassFieldVarInst;
    private final Type defaultType;

    /**
     * Instantiates a new Var inst with lookup.
     *
     * @param varInst     the var inst
     * @param lookupClassFieldVarInst the lookup class
     */
    public VarInstWithLookup(VarInst varInst, ClassTypeFieldVarInst lookupClassFieldVarInst) {
        this.varInst = varInst;
        this.lookupClassFieldVarInst = lookupClassFieldVarInst;
        this.defaultType = null;
    }

    /**
     * Instantiates a new Var inst with lookup.
     *
     * @param varInst     the var inst
     * @param lookupClassFieldVarInst the lookup class
     * @param defaultType the default type
     */
    public VarInstWithLookup(VarInst varInst, ClassTypeFieldVarInst lookupClassFieldVarInst, Type defaultType) {
        this.varInst = varInst;
        this.lookupClassFieldVarInst = lookupClassFieldVarInst;
        this.defaultType = defaultType;
    }

    @Override
    public Action load() {
        return varInst;
    }

    @Override
    public Type getType() {
        return varInst.getType();
    }

    /**
     * Gets lookup class.
     *
     * @return the lookup class
     */
    public ClassTypeFieldVarInst getLookupClass() {
        return lookupClassFieldVarInst;
    }

    /**
     * Default type type.
     *
     * @return the type
     */
    public Type defaultType() {
        return defaultType;
    }
}
