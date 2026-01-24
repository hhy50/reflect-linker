package io.github.hhy50.linker.generate.bytecode;

import io.github.hhy50.linker.asm.AsmField;
import io.github.hhy50.linker.generate.bytecode.action.ClassTypeVarInst;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.Runtime;

/**
 * The type Class type member.
 */
public class ClassTypeMember extends Member implements ClassTypeVarInst {

    /**
     * Instantiates a new Class type member.
     *
     * @param field the member
     */
    public ClassTypeMember(AsmField field) {
        super(field);
    }

    @Override
    public VarInst getLookup() {
        return new MethodInvokeAction(Runtime.LOOKUP).setArgs(this);
    }
}
