package io.github.hhy50.linker.generate.bytecode;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.vars.ClassVar;
import io.github.hhy50.linker.generate.bytecode.vars.LookupVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.Runtime;
import org.objectweb.asm.Opcodes;

/**
 * <p>ClassTypeMember class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class ClassTypeMember extends Member {

    private boolean inited;

    /**
     * <p>Constructor for Member.</p>
     *
     * @param access     a int.
     * @param owner      a {@link java.lang.String} object.
     * @param memberName a {@link java.lang.String} object.
     */
    public ClassTypeMember(int access, String owner, String memberName) {
        super(access, owner, memberName, ClassVar.TYPE);
    }

    /**
     * <p>getLookup.</p>
     *
     * @param body a {@link io.github.hhy50.linker.generate.MethodBody} object.
     * @return a {@link io.github.hhy50.linker.generate.bytecode.vars.VarInst} object.
     */
    public VarInst getLookup(MethodBody body) {
        if ((this.access & Opcodes.ACC_STATIC) > 0) {

        }
        return body.newLocalVar(LookupVar.TYPE, new MethodInvokeAction(Runtime.LOOKUP)
                .setArgs(this));
    }

    /**
     * <p>staticInit.</p>
     *
     * @param clinit a {@link io.github.hhy50.linker.generate.MethodBody} object.
     * @param classLoadAction a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     */
    public void staticInit(MethodBody clinit, Action classLoadAction) {
        if (inited) return;
        this.store(clinit, classLoadAction);
        inited = true;
    }
}
