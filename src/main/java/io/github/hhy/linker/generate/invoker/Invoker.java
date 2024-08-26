package io.github.hhy.linker.generate.invoker;

import io.github.hhy.linker.define.method.MethodRef;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.MethodHandle;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import io.github.hhy.linker.generate.getter.Getter;

public class Invoker extends MethodHandle {
    private final Getter<?> owner;
    private final MethodRef methodRef;

    public Invoker(Getter<?> owner, MethodRef methodRef) {
        this.owner = owner;
        this.methodRef = methodRef;
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        this.owner.define(classImplBuilder);
//        super.define0(classImplBuilder);



    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        return null;
    }

    @Override
    protected void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, VarInst objVar) {

    }
}
