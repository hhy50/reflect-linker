package io.github.hhy.linker.generate.setter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.define.field.RuntimeFieldRef;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import io.github.hhy.linker.generate.getter.Getter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


public class RuntimeFieldSetter extends Setter<RuntimeFieldRef> {

    public RuntimeFieldSetter(String implClass, RuntimeFieldRef field) {
        super(implClass, field);
    }

    @Override
    public final void define0(InvokeClassImplBuilder classImplBuilder) {
        Getter<?> getter = classImplBuilder.getGetter(field.getPrev().getUniqueName());
        getter.define(classImplBuilder);

        // 先定义上一层字段的lookup
        LookupMember lookupMember = classImplBuilder.defineLookup(field.getPrev());
        // 定义当前字段的mh
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(field.getSetterName(), methodType);

        // 定义当前字段的getter
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "").accept(mv -> {
            MethodBody methodBody = new MethodBody(mv, methodType);
            VarInst objVar = getter.invoke(methodBody);

            if (!lookupMember.isTargetLookup()) {
                // 校验lookup和mh
                LookupMember preLookup = classImplBuilder.defineLookup(field.getPrev().getPrev());
                staticCheckLookup(methodBody, preLookup, lookupMember, objVar, field.getPrev());
                checkLookup(methodBody, lookupMember, mhMember, objVar);
            }
            checkMethodHandle(methodBody, lookupMember, mhMember, objVar);

            // mh.invoke(obj, fieldValue)
            VarInst vold = mhMember.invoke(methodBody, objVar, methodBody.getArg(0));
            AsmUtil.areturn(mv, Type.VOID_TYPE);
        });
    }
}
