package io.github.hhy.linker.bytecode.setter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.bytecode.InvokeClassImplBuilder;
import io.github.hhy.linker.bytecode.MethodBody;
import io.github.hhy.linker.bytecode.MethodHandle;
import io.github.hhy.linker.bytecode.vars.LookupMember;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import io.github.hhy.linker.define.MethodDefine;
import org.objectweb.asm.Type;

public class SetterWrapper extends MethodHandle {

    private Setter setter;
    private final MethodDefine methodDefine;

    public SetterWrapper(Setter setter, MethodDefine methodDefine) {
        this.setter = setter;
        this.methodDefine = methodDefine;
    }

    @Override
    public void define(InvokeClassImplBuilder classImplBuilder) {
        setter.define(classImplBuilder);
    }

    @Override
    public ObjectVar invoke(MethodBody methodBody) {
        setter.invoke(methodBody);
        AsmUtil.areturn(methodBody.writer, Type.VOID_TYPE);
        return null;
    }

    @Override
    public void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar) {
        setter.mhReassign(methodBody, lookupMember, mhMember, objVar);
    }
}
