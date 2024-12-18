package io.github.hhy50.linker.generate.bytecode;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.vars.LookupVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.Runtime;
import org.objectweb.asm.Opcodes;

/**
 * The type Class type member.
 */
public class ClassTypeMember extends Member {

    private boolean inited;

    /**
     * Instantiates a new Class type member.
     *
     * @param member the member
     */
    public ClassTypeMember(Member member) {
        super(member.access, member.owner, member.memberName, member.type);
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
        return body.newLocalVar(LookupVar.TYPE, new MethodInvokeAction(Runtime.LOOKUP).setArgs(this));
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
}
