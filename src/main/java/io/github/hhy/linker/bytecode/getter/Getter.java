package io.github.hhy.linker.bytecode.getter;

import io.github.hhy.linker.bytecode.MethodBody;
import io.github.hhy.linker.bytecode.MethodHandle;
import io.github.hhy.linker.bytecode.vars.LookupMember;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import io.github.hhy.linker.define.field2.FieldRef;
import io.github.hhy.linker.runtime.Runtime;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public abstract class Getter<T extends FieldRef> extends MethodHandle {

    protected final T field;

    public Getter(T field) {
        this.field = field;
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
