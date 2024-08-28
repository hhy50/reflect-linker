package io.github.hhy.linker.generate.invoker;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.define.field.EarlyFieldRef;
import io.github.hhy.linker.define.method.EarlyMethodRef;
import io.github.hhy.linker.entity.MethodHolder;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.action.LdcLoadAction;
import io.github.hhy.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import io.github.hhy.linker.generate.getter.Getter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class EarlyMethodInvoker extends Invoker<EarlyMethodRef> {
    private Type methodType;

    public EarlyMethodInvoker(String implClass, EarlyMethodRef methodRef) {
        super(implClass, methodRef, methodRef.getMethodType());
        this.methodType = methodRef.getMethodType();
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        EarlyFieldRef owner = (EarlyFieldRef) method.getOwner();
        Getter<?> getter = classImplBuilder.defineGetter(owner.getUniqueName(), owner);
        getter.define(classImplBuilder);

        MethodBody clinit = classImplBuilder.getClinit();
        // init lookup
        LookupMember lookupMember = classImplBuilder.defineLookup(owner);
        lookupMember.staticInit(clinit);

        // init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(method.getFullName(), this.methodType);
        initStaticMethodHandle(classImplBuilder, mhMember, lookupMember, owner.getDeclaredType(), method.getName(), methodType, method.isStatic());

        // 定义当前方法的invoker
        classImplBuilder
                .defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "")
                .accept(mv -> {
                    MethodBody methodBody = new MethodBody(mv, methodType);
                    VarInst objVar = getter.invoke(methodBody);
                    if (!method.isStatic()) {
                        objVar.checkNullPointer(methodBody, objVar.getName());
                    }

                    // mh.invoke(obj)
                    VarInst result = method.isStatic() ? mhMember.invokeStatic(methodBody) : mhMember.invokeInstance(methodBody, objVar);
                    if (result != null) {
                        result.load(methodBody);
                    }
                    AsmUtil.areturn(mv, methodType.getReturnType());
                });
    }

    @Override
    protected void initStaticMethodHandle(InvokeClassImplBuilder classImplBuilder, MethodHandleMember mhMember, LookupMember lookupMember, Type ownerType, String fieldName, Type methodType, boolean isStatic) {
        MethodBody clinit = classImplBuilder.getClinit();
        mhMember.store(clinit, new MethodInvokeAction(isStatic ? MethodHolder.LOOKUP_FIND_STATIC_SETTER_METHOD : MethodHolder.LOOKUP_FIND_SETTER_METHOD)
                .setInstance(lookupMember)
                .setArgs(LdcLoadAction.of(ownerType), LdcLoadAction.of(fieldName), LdcLoadAction.of(methodType.getArgumentTypes()[0])));
    }

    @Override
    protected void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, VarInst objVar) {
        // mh = Runtime.findGetter(lookup, "field");
//        MethodInvokeAction findGetter = new MethodInvokeAction(Runtime.FIND_GETTER)
//                .setArgs(lookupMember, LdcLoadAction.of(field.fieldName));
//        mhMember.store(methodBody, findGetter);
    }
}
