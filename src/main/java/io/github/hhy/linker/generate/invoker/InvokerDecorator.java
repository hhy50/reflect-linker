package io.github.hhy.linker.generate.invoker;

import io.github.hhy.linker.define.MethodDefine;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.MethodHandle;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;

public class InvokerDecorator extends MethodHandle {

    protected Invoker<?> realInvoker;
    private final MethodDefine methodDefine;

    public InvokerDecorator(String implClass, Invoker<?> realInvoker, MethodDefine methodDefine) {
        this.realInvoker = realInvoker;
        this.methodDefine = methodDefine;
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        this.realInvoker.define(classImplBuilder);
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        return null;
    }

    @Override
    protected void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, VarInst objVar) {
        throw new RuntimeException("Decorator not impl mhReassign() method");
    }
}
