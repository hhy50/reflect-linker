package io.github.hhy50.linker.generate.bytecode;

import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.vars.ClassVar;
import io.github.hhy50.linker.generate.bytecode.vars.LookupVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.Runtime;
import org.objectweb.asm.Type;

public class ClassTypeMember extends Member {

    private Type classType;

    private InvokeClassImplBuilder classImplBuilder;

    private boolean inited;

    /**
     * <p>Constructor for Member.</p>
     *
     * @param access     a int.
     * @param owner      a {@link String} object.
     * @param memberName a {@link String} object.
     * @param classType
     */
    public ClassTypeMember(int access, String owner, String memberName, Type classType) {
        super(access, owner, memberName, ClassVar.TYPE);
        this.classType = classType;
    }

    public void setClassImplBuilder(InvokeClassImplBuilder classImplBuilder) {
        this.classImplBuilder = classImplBuilder;
    }

    public VarInst getLookup(MethodBody body) {
        return body.newLocalVar(LookupVar.TYPE, new MethodInvokeAction(Runtime.LOOKUP)
                .setArgs(this));
    }

    /**
     * <pre>
     *     if(obj != null) {
     *         class = obj.getClass();
     *     } else {
     *         class = Runtime.getClass('');
     *     }
     * </pre>
     *
     * @param body
     * @param var
     * @param classLoadAction
     */
    public void store(MethodBody body, Action classLoadAction) {
        if (inited) return;
        super.store(body, classLoadAction);
        this.inited = true;
    }

    public void checkClass(MethodBody body, VarInst var, Action classLoadAction) {
        var.ifNull(body,
                classLoadAction != null ? (__) -> store(body, classLoadAction) : null,
                (__) -> store(body, var.getThisClass())
        );
    }
}
