package io.github.hhy.linker.generate.invoker;

import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.MethodHandle;
import io.github.hhy.linker.generate.bytecode.vars.LookupMember;
import io.github.hhy.linker.generate.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.vars.ObjectVar;

public class Invoker extends MethodHandle {

    @Override
    public ObjectVar invoke(MethodBody methodBody) {
        return null;
    }

    @Override
    protected void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar) {

    }
}
