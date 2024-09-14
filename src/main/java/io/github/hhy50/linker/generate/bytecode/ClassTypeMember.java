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

//    public void staticInit(MethodBody body, Action action) {
//        // TODO
//        this.store(body, action);
//    }

    public VarInst getLookup(MethodBody body) {
        return body.newLocalVar(LookupVar.TYPE, new MethodInvokeAction(Runtime.LOOKUP)
                .setArgs(this));
    }
}
