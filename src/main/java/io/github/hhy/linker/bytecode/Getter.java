package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.bytecode.vars.LookupMember;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.MethodHandleVar;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import io.github.hhy.linker.define.RuntimeField;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.*;

public class Getter extends MethodHandle {

    private RuntimeField field;
    private final LookupMember lookupMember;
    private final MethodHandleMember mhMember;

    public Getter(String bindImplClass, MethodHandle prev, RuntimeField field) {
        super(prev);
        this.field = field;
        this.lookupMember = field.getPrev() != null ? new LookupMember(bindImplClass, field.getPrev().getFullName()+"_lookup") : null;
        this.mhMember = new MethodHandleMember(bindImplClass, field.getFullName()+"_getter_mh");
    }

    @Override
    public void define(InvokeClassImplBuilder classImplBuilder) {
        prevMethodHandle.define(classImplBuilder);

        RuntimeField prev = field.getPrev();
        if (prev != null) {
            String prevFullName = field.getPrev().getFullName();
            // 先定义上一层字段的lookup
            classImplBuilder.defineField(Opcodes.ACC_PUBLIC, lookupMember.memberName, lookupMember.type, null, null);
        }

        // 定义当前字段的mh
        classImplBuilder.defineField(Opcodes.ACC_PUBLIC, field.getGetterMhVarName(), MethodHandleVar.DESCRIPTOR, null, null);
    }

    @Override
    public ObjectVar invoke(MethodBody methodBody) {
        ObjectVar prevObj = prevMethodHandle.invoke(methodBody);
        prevObj.checkNullPointer(methodBody, field.getNullErrorVar());

        //
        checkLookup(methodBody, lookupMember, mhMember, prevObj);


        return null;
    }

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
