package io.github.hhy50.linker.generate.bytecode;

import io.github.hhy50.linker.asm.AsmField;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.vars.ClassTypeVarInst;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.Runtime;

/**
 * The type Class type field.
 */
public class ClassTypeFieldVarInst extends FieldVarInst implements ClassTypeVarInst {

    /**
     * Instantiates a new Class type field.
     *
     * @param field the field
     */
    public ClassTypeFieldVarInst(AsmField field) {
        super(field);
    }

    @Override
    public VarInst getLookup() {
        return new MethodInvokeAction(Runtime.LOOKUP).setArgs(this);
    }
}
