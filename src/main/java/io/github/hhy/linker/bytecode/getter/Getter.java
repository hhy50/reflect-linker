package io.github.hhy.linker.bytecode.getter;

import io.github.hhy.linker.bytecode.InvokeClassImplBuilder;
import io.github.hhy.linker.bytecode.MethodBody;
import io.github.hhy.linker.bytecode.MethodHandle;
import io.github.hhy.linker.bytecode.MethodHolder;
import io.github.hhy.linker.bytecode.vars.LookupMember;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.runtime.Runtime;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public abstract class Getter<T extends FieldRef> extends MethodHandle {

    private boolean defined = false;

    public final T field;

    public MethodHolder methodHolder;

    protected LookupMember lookupMember;

    public Getter(T field) {
        this.field = field;
    }

    public final void define(InvokeClassImplBuilder classImplBuilder) {
        if (defined) return;
        define0(classImplBuilder);
        this.defined = true;
    }

    protected void define0(InvokeClassImplBuilder classImplBuilder) {

    }

    @Override
    protected void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar) {
        methodBody.append(mv -> {
            lookupMember.load(methodBody); // this.lookup
            mv.visitLdcInsn(this.field.fieldName); // 'field'
            mv.visitMethodInsn(INVOKESTATIC, Runtime.OWNER, "findGetter", Runtime.FIND_GETTER_DESC, false);
            mhMember.store(methodBody);
        });
    }
}
