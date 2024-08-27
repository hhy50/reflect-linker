package io.github.hhy.linker.generate.invoker;

import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.define.method.MethodRef;
import io.github.hhy.linker.entity.MethodHolder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.MethodHandle;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

public class Invoker<T extends MethodRef> extends MethodHandle {
    protected final FieldRef owner;
    protected final T methodRef;
    protected Type methodType;
    protected MethodHolder methodHolder;

    public Invoker(String implClass, FieldRef owner, T methodRef, Type methodType) {
        this.owner = owner;
        this.methodRef = methodRef;
        this.methodType = methodType;
        this.methodHolder = new MethodHolder(implClass, methodRef.getFullName(), methodType.getDescriptor());
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        return null;
    }

    @Override
    protected void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, VarInst objVar) {

    }
}
