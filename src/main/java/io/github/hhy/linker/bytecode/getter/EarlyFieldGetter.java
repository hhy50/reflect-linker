package io.github.hhy.linker.bytecode.getter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.bytecode.InvokeClassImplBuilder;
import io.github.hhy.linker.bytecode.MethodBody;
import io.github.hhy.linker.bytecode.vars.LookupMember;
import io.github.hhy.linker.bytecode.vars.LookupVar;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import io.github.hhy.linker.define.field.EarlyFieldRef;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static io.github.hhy.linker.asm.AsmUtil.adaptLdcClassType;

public class EarlyFieldGetter extends Getter<EarlyFieldRef> {

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
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "").accept(mv -> {
            MethodBody methodBody = new MethodBody(mv, methodType);
            ObjectVar objVar = getter.invoke(methodBody);

            // mh.invoke(obj)
            ObjectVar result = field.isStatic() ? mhMember.invokeStatic(methodBody) : mhMember.invokeInstance(methodBody, objVar);
            result.load(methodBody);
            AsmUtil.areturn(mv, methodType.getReturnType());
        });
    }

    @Override
    protected void initStaticMethodHandle(InvokeClassImplBuilder classImplBuilder, MethodHandleMember mhMember, LookupMember lookupMember, Type ownerType, String fieldName, Type methodType, boolean isStatic) {
        MethodBody clinit = classImplBuilder.getClinit();
        clinit.append(mv -> {
            // mh = lookup.findGetter(ArrayList.class, "elementData", Object[].class);
            lookupMember.load(clinit); // lookup
            mv.visitLdcInsn(ownerType); // ArrayList.class
            mv.visitLdcInsn(fieldName); // 'elementData'
            adaptLdcClassType(mv, methodType.getReturnType()); // Object[].class

            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, LookupVar.OWNER, isStatic ? "findStaticGetter" : "findGetter", LookupVar.FIND_GETTER_DESC, false);
            mhMember.store(clinit);
        });
    }
}
