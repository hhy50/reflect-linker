package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.bytecode.vars.LookupMember;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.MethodHandleVar;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import io.github.hhy.linker.define.RuntimeField;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class RuntimeFieldGetter extends Getter {
    public RuntimeField prev;
    private final LookupMember lookupMember;
    private final MethodHandleMember mhMember;

    public RuntimeFieldGetter(String implClass, RuntimeField field) {
        super(field);
        this.prev = field.getPrev();
        this.lookupMember = new LookupMember(ClassUtil.className2path(implClass), this.prev.getFullName()+"_lookup");
        this.mhMember = new MethodHandleMember(ClassUtil.className2path(implClass), field.getGetterMhVarName(), "(Ljava/lang/Object;)Ljava/lang/Object;");
    }

    @Override
    public void define(InvokeClassImplBuilder classImplBuilder) {
        this.prev.getter.define(classImplBuilder);
        // 先定义上一层字段的lookup
        classImplBuilder.defineField(Opcodes.ACC_PUBLIC, lookupMember.memberName, lookupMember.type, null, null);
        // 定义当前字段的mh
        classImplBuilder.defineField(Opcodes.ACC_PUBLIC, field.getGetterMhVarName(), MethodHandleVar.DESCRIPTOR, null, null);
    }

    @Override
    public ObjectVar invoke(MethodBody methodBody) {
        ObjectVar prevObj = prev.getter.invoke(methodBody);
        prevObj.checkNullPointer(methodBody, field.getNullErrorVar());

        // 校验lookup和mh
        checkLookup(methodBody, lookupMember, mhMember, prevObj);
        // mh.invoke()
        mhMember.invoke(methodBody, prevObj);
        AsmUtil.areturn(methodBody.writer, methodDefine == null ? Type.getType(Object.class) : Type.getType(methodDefine.define.getReturnType()));
        return null;
    }
}
