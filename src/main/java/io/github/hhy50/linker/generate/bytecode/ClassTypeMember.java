package io.github.hhy50.linker.generate.bytecode;

import io.github.hhy50.linker.asm.AsmField;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.ClassLoadAction;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.Runtime;
import org.objectweb.asm.Opcodes;

/**
 * The type Class type member.
 */
public class ClassTypeMember extends Member implements ClassLoadAction {

    private boolean inited;

    /**
     * Instantiates a new Class type member.
     *
     * @param field the member
     */
    public ClassTypeMember(AsmField field) {
        super(field);
    }

    /**
     * Gets lookup.
     *
     * @param body the body
     * @return the lookup
     */
    public VarInst getLookup(MethodBody body) {
        if ((this.access & Opcodes.ACC_STATIC) > 0) {

        }
        return body.newLocalVar(new MethodInvokeAction(Runtime.LOOKUP).setArgs(this));
    }

    /**
     * Static init.
     *
     * @param clinit          the clinit
     * @param classLoadAction the class load action
     */
    public void staticInit(MethodBody clinit, Action classLoadAction) {
        if (inited) return;
        this.store(clinit, classLoadAction);
        inited = true;
    }

    @Override
    public Action getLookup() {
        return this::getLookup;
    }
}
