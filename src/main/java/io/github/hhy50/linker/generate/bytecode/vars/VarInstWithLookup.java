package io.github.hhy50.linker.generate.bytecode.vars;

import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import org.objectweb.asm.Type;

/**
 * The type Var inst with lookup.
 */
public class VarInstWithLookup extends VarInst {

    private final VarInst varInst;
    private final ClassTypeMember lookupClass;
    private final Type defaultType;

    /**
     * Instantiates a new Var inst with lookup.
     *
     * @param varInst     the var inst
     * @param lookupClass the lookup class
     */
    public VarInstWithLookup(VarInst varInst, ClassTypeMember lookupClass) {
        this.varInst = varInst;
        this.lookupClass = lookupClass;
        this.defaultType = null;
    }

    /**
     * Instantiates a new Var inst with lookup.
     *
     * @param varInst     the var inst
     * @param lookupClass the lookup class
     * @param defaultType the default type
     */
    public VarInstWithLookup(VarInst varInst, ClassTypeMember lookupClass, Type defaultType) {
        this.varInst = varInst;
        this.lookupClass = lookupClass;
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
    public ClassTypeMember getLookupClass() {
        return lookupClass;
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
