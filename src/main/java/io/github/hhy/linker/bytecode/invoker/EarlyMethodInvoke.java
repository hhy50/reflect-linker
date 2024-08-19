package io.github.hhy.linker.bytecode.invoker;

import io.github.hhy.linker.bytecode.MethodBody;
import io.github.hhy.linker.bytecode.MethodHandle;
import io.github.hhy.linker.bytecode.vars.LookupMember;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.ObjectVar;

public class EarlyMethodInvoke extends MethodHandle {

    public EarlyMethodInvoke() {

    }

    @Override
    public ObjectVar invoke(MethodBody methodBody) {
        return null;
    }

    @Override
    protected void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar) {

    }
}
