package io.github.hhy.linker.code;

import io.github.hhy.linker.bytecode.InvokeClassImplBuilder;
import io.github.hhy.linker.code.vars.*;
import io.github.hhy.linker.define.Field;
import org.objectweb.asm.Opcodes;

public class Getter extends MethodHandle {

    private Field field;

    private final LookupMember lookupMember;
    private final MethodHandleMember mhMember;

    public Getter(String targetClass, MethodHandle prev, Field field) {
        super(prev);
        this.field = field;
        this.lookupMember = field.getPrev() != null ? new LookupMember(targetClass, field.getPrev().getFullName() + "_lookup"):null;
        this.mhMember = new MethodHandleMember(targetClass, field.getFullName() + "_getter_mh", field);
    }

    @Override
    public void define(InvokeClassImplBuilder classImplBuilder) {
        prevMethodHandle.define(classImplBuilder);

        Field prev = field.getPrev();
        if (prev != null) {
            String prevFullName = field.getPrev().getFullName();
            // 先定义上一层字段的lookup
            classImplBuilder.defineField(Opcodes.ACC_PUBLIC, lookupMember., LookupVar.DESCRIPTOR, null, null);
        }

        // 定义当前字段的mh
        this.mhField = field.getFullName() + "_getter_mh";
        classImplBuilder.defineField(Opcodes.ACC_PUBLIC, mhField, MethodHandleVar.DESCRIPTOR, null, null);
    }

    @Override
    public ObjectVar invoke(MethodBody methodBody) {
        ObjectVar prevObj = prevMethodHandle.invoke(methodBody);
        prevObj.checkNullPointer(methodBody, field.getFieldName() + "[null]");

        lookupMember.checkLookup(methodBody, prevObj);
        mhMember.checkLookup(methodBody, lookupMember, prevObj);


        return null;
    }
}
