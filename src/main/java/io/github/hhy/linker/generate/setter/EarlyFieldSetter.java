package io.github.hhy.linker.generate.setter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.define.field.EarlyFieldRef;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import io.github.hhy.linker.generate.getter.Getter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class EarlyFieldSetter extends Setter<EarlyFieldRef> {

    public EarlyFieldSetter(String implClass, EarlyFieldRef field) {
        super(implClass, field);
    }

    @Override
    public final void define0(InvokeClassImplBuilder classImplBuilder) {
        Getter<?> getter = classImplBuilder.getGetter(field.getPrev().getFullName());
        getter.define(classImplBuilder);

        MethodBody clinit = classImplBuilder.getClinit();
        // 定义上一层字段的lookup, 必须要用declaredType
        LookupMember lookupMember = classImplBuilder.defineLookup(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, field.declaredType);
        // init lookup
        lookupMember.staticInit(clinit);

        // 定义当前字段的 setter
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(field.getSetterName(), this.methodType);
        // init methodHandle
        initStaticMethodHandle(classImplBuilder, mhMember, lookupMember, field.declaredType, field.fieldName, methodType, field.isStatic());

        // 定义当前字段的 setter
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "").accept(mv -> {
            MethodBody methodBody = new MethodBody(mv, methodType);
            VarInst objVar = getter.invoke(methodBody);

            // mh.invoke(obj)
            VarInst result = field.isStatic() ? mhMember.invokeStatic(methodBody, methodBody.getArg(0)) : mhMember.invokeInstance(methodBody, objVar, methodBody.getArg(0));
            AsmUtil.areturn(mv, Type.VOID_TYPE);
        });
    }
}
