package io.github.hhy.linker.bytecode.getter;

import io.github.hhy.linker.bytecode.InvokeClassImplBuilder;
import io.github.hhy.linker.bytecode.MethodBody;
import io.github.hhy.linker.bytecode.MethodHandle;
import io.github.hhy.linker.bytecode.vars.LookupMember;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import io.github.hhy.linker.define.RuntimeField;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.*;

public abstract class Getter extends MethodHandle {

    private boolean defined = false;

    protected final RuntimeField field;

    public Getter(RuntimeField field) {
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
            mv.visitVarInsn(Opcodes.ALOAD, 0); // this
            mv.visitVarInsn(Opcodes.ALOAD, 0); // this
            mv.visitFieldInsn(GETFIELD, lookupMember.owner, lookupMember.memberName, lookupMember.type); // this.lookup
            mv.visitVarInsn(ALOAD, objVar.lvbIndex); // obj
            mv.visitLdcInsn(this.field.fieldName); // 'field'
            mv.visitMethodInsn(INVOKESTATIC, "io/github/hhy/linker/runtime/Runtime", "findGetter", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitFieldInsn(PUTFIELD, mhMember.owner, mhMember.memberName, mhMember.type);
        });
    }
}
