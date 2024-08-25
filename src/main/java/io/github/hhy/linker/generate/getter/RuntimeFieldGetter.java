package io.github.hhy.linker.generate.getter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.define.field.RuntimeFieldRef;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Opcodes;

public class RuntimeFieldGetter extends Getter<RuntimeFieldRef> {


    public RuntimeFieldGetter(String implClass, RuntimeFieldRef field) {
        super(implClass, field);
    }

    @Override
    public void define0(InvokeClassImplBuilder classImplBuilder) {
        Getter<?> getter = classImplBuilder.getGetter(field.getPrev().getFullName());
        getter.define(classImplBuilder);

        // 先定义上一层字段的lookup
        LookupMember lookupMember = classImplBuilder.defineLookup(field.getPrev());
        // 定义当前字段的mh
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(field.getGetterName(), methodType);

        // 定义当前字段的getter
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "").accept(mv -> {
            MethodBody methodBody = new MethodBody(mv, methodType);
            VarInst objVar = getter.invoke(methodBody);

            if (!lookupMember.isTargetLookup()) {
                // 校验lookup和mh
//                Getter prev = this.prev.getter;
//                LookupMember lookupMember2 = classImplBuilder.defineLookup(field.getPrev());
//                staticCheckLookup(methodBody, lookupMember2, lookupMember, objVar, prev.field);
                checkLookup(methodBody, lookupMember, mhMember, objVar);
            }
            checkMethodHandle(methodBody, lookupMember, mhMember, objVar);

            // mh.invoke(obj)
            VarInst result = mhMember.invoke(methodBody, objVar);
            result.load(methodBody);
            AsmUtil.areturn(mv, methodType.getReturnType());
        });
    }
}
