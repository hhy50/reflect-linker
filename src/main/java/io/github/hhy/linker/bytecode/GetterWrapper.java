package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.bytecode.vars.LookupMember;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.MethodHandleVar;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import io.github.hhy.linker.define.RuntimeField;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.*;

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
        ObjectVar invoke = getter.invoke();
        return null;
    }

//    @Override
//    public ObjectVar invoke(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar) {
//        ObjectVar prevObj = field.getter.invoke(methodBody);
//        prevObj.checkNullPointer(methodBody, field.getNullErrorVar());
//
//        // 校验lookup和mh
//        checkLookup(methodBody, lookupMember, mhMember, prevObj);
//
//        return null;
//    }

    @Override
    public void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar) {
        methodBody.append(mv -> {
            mv.visitVarInsn(Opcodes.ALOAD, 0); // this
            mv.visitFieldInsn(GETFIELD, lookupMember.owner, lookupMember.memberName, lookupMember.type); // this.lookup
            mv.visitVarInsn(ALOAD, objVar.lvbIndex); // obj
            mv.visitLdcInsn(this.field.fieldName); // 'field'
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Runtime", "findGetter", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitFieldInsn(PUTFIELD, mhMember.owner, mhMember.memberName, mhMember.type);
        });
    }
}
