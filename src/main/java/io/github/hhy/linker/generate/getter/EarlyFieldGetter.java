package io.github.hhy.linker.generate.getter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.constant.Lookup;
import io.github.hhy.linker.define.field.EarlyFieldRef;
import io.github.hhy.linker.entity.MethodHolder;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.action.LdcLoadAction;
import io.github.hhy.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


public class EarlyFieldGetter extends Getter<EarlyFieldRef> {
    private static final MethodHolder FIND_GETTER_METHOD = new MethodHolder(Lookup.OWNER, "findGetter", Lookup.FIND_GETTER_DESC);
    private static final MethodHolder FIND_STATIC_GETTER_METHOD = new MethodHolder(Lookup.OWNER, "findStaticGetter", Lookup.FIND_GETTER_DESC);

    public EarlyFieldGetter(String implClass, EarlyFieldRef fieldRef) {
        super(implClass, fieldRef);
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        Getter<?> getter = classImplBuilder.getGetter(field.getPrev().getFullName());
        getter.define(classImplBuilder);

        MethodBody clinit = classImplBuilder.getClinit();

        // 定义上一层字段的lookup, 必须要用declaredType
        LookupMember lookupMember = classImplBuilder.defineLookup(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, field.declaredType);
        // init lookup
        lookupMember.staticInit(clinit);

        // 定义当前字段的getter mh
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(field.getGetterName(), this.methodType);
        // init methodHandle
        initStaticMethodHandle(classImplBuilder, mhMember, lookupMember, field.declaredType, field.fieldName, methodType, field.isStatic());

        // 定义当前字段的getter
        classImplBuilder
                .defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "")
                .accept(mv -> {
                    MethodBody methodBody = new MethodBody(mv, methodType);
                    VarInst objVar = getter.invoke(methodBody);

                    // mh.invoke(obj)
                    VarInst result = field.isStatic() ? mhMember.invokeStatic(methodBody) : mhMember.invokeInstance(methodBody, objVar);
                    result.load(methodBody);
                    AsmUtil.areturn(mv, methodType.getReturnType());
                });
    }

    @Override
    protected void initStaticMethodHandle(InvokeClassImplBuilder classImplBuilder, MethodHandleMember mhMember, LookupMember lookupMember, Type ownerType, String fieldName, Type methodType, boolean isStatic) {
        // mh = lookup.findGetter(ArrayList.class, "elementData", Object[].class);
        MethodInvokeAction findGetter = new MethodInvokeAction(isStatic ? FIND_STATIC_GETTER_METHOD : FIND_GETTER_METHOD);
        findGetter.setInstance(lookupMember).setArgs(LdcLoadAction.of(ownerType), LdcLoadAction.of(fieldName), LdcLoadAction.of(methodType.getReturnType()));
        mhMember.store(classImplBuilder.getClinit(), findGetter);
    }
}
