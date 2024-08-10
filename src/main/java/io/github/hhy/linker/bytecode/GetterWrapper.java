package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.bytecode.vars.LookupMember;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.ObjectVar;

public class GetterWrapper extends MethodHandle {

    private Getter getter;

    public GetterWrapper(Getter getter) {
        this.getter = getter;
    }

    @Override
    public void define(InvokeClassImplBuilder classImplBuilder) {
        getter.define(classImplBuilder);
    }

    @Override
    public ObjectVar invoke(MethodBody methodBody) {
        ObjectVar result = getter.invoke(methodBody);
        return result;
    }

    @Override
    public void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar) {
        getter.mhReassign(methodBody, lookupMember, mhMember, objVar);
    }
}
