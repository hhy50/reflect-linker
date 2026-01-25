package io.github.hhy50.linker.generate.bytecode.vars;

import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import org.objectweb.asm.Type;

public class VarInstWithLookup extends VarInst {

    private final VarInst varInst;
    private final ClassTypeMember lookupClass;

    public VarInstWithLookup(VarInst varInst, ClassTypeMember lookupClass) {
        this.varInst = varInst;
        this.lookupClass = lookupClass;
    }

    @Override
    public Action load() {
        return varInst;
    }

    @Override
    public Type getType() {
        return varInst.getType();
    }

    public ClassTypeMember getLookupClass() {
        return lookupClass;
    }
}
