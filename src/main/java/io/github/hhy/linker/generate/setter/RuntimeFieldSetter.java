package io.github.hhy.linker.generate.setter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.define.field.RuntimeFieldRef;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.vars.LookupMember;
import io.github.hhy.linker.generate.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy.linker.generate.getter.Getter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


public class RuntimeFieldSetter extends Setter<RuntimeFieldRef> {

    public RuntimeFieldSetter(String implClass, RuntimeFieldRef field) {
        super(implClass, field);
    }

    @Override
    public final void define0(InvokeClassImplBuilder classImplBuilder) {
        Getter<?> getter = classImplBuilder.getGetter(field.getPrev().getFullName());
        getter.define(classImplBuilder);

        // 先定义上一层字段的lookup
        LookupMember lookupMember = classImplBuilder.defineLookup(field.getPrev());
        // 定义当前字段的mh
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(field.getSetterName(), methodType);

        // 定义当前字段的getter
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "").accept(mv -> {
            MethodBody methodBody = new MethodBody(mv, methodType);
            ObjectVar objVar = getter.invoke(methodBody);

            if (!lookupMember.isTargetLookup()) {
                // 校验lookup和mh
//                Getter prev = this.prev.getter;
//                LookupMember lookupMember2 = classImplBuilder.defineLookup(field.getPrev());
//                staticCheckLookup(methodBody, lookupMember2, lookupMember, objVar, prev.field);
                checkLookup(methodBody, lookupMember, mhMember, objVar);
            }
            checkMethodHandle(methodBody, lookupMember, mhMember, objVar);

            // mh.invoke(obj, fieldValue)
            ObjectVar result = mhMember.invoke(methodBody, objVar, methodBody.getArg(0));
//            result.load(methodBody);
            AsmUtil.areturn(mv, Type.VOID_TYPE);
        });
    }
}
