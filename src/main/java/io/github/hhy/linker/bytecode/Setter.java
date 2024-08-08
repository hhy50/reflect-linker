package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.bytecode.vars.LookupMember;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.ObjectVar;

public class Setter extends MethodHandle {


    @Override
    public ObjectVar invoke(MethodBody methodBody) {
        return null;
    }

    @Override
    protected void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar) {

    }
}
