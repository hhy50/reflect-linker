package io.github.hhy.linker.generate.invoker;

import io.github.hhy.linker.define.field.EarlyFieldRef;
import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.define.method.MethodRef;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.getter.Getter;

public class EarlyMethodInvoker extends Invoker {
    public EarlyMethodInvoker(String implClass, FieldRef owner, MethodRef methodRef) {
        super(implClass, owner, methodRef, null);
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        Getter<?> getter = classImplBuilder.defineGetter(owner.getFullName(), owner);
        getter.define(classImplBuilder);


        if (owner instanceof EarlyFieldRef) {
            MethodBody clinit = classImplBuilder.getClinit();
            // init lookup
            LookupMember lookupMember = classImplBuilder.defineLookup(owner);
            lookupMember.staticInit(clinit);

            // init methodHandle
//            MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(field.getGetterName(), this.methodType);
//            initStaticMethodHandle(classImplBuilder, mhMember, lookupMember, field.declaredType, field.fieldName, methodType, field.isStatic());
        }

        // 定义当前方法的invoker
//        classImplBuilder
//                .defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "")
//                .accept(mv -> {
//                    MethodBody methodBody = new MethodBody(mv, methodType);
//                    VarInst objVar = getter.invoke(methodBody);
//                    if (!field.isStatic()) {
//                        objVar.checkNullPointer(methodBody, objVar.getName());
//                    }
//
//                    // mh.invoke(obj)
//                    VarInst result = field.isStatic() ? mhMember.invokeStatic(methodBody) : mhMember.invokeInstance(methodBody, objVar);
//                    result.load(methodBody);
//                    AsmUtil.areturn(mv, methodType.getReturnType());
//                });
    }


}
