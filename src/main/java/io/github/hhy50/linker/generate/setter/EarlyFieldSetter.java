package io.github.hhy50.linker.generate.setter;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.entity.MethodHolder;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.LookupMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.LdcLoadAction;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.getter.Getter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * <p>EarlyFieldSetter class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class EarlyFieldSetter extends Setter<EarlyFieldRef> {

    /**
     * <p>Constructor for EarlyFieldSetter.</p>
     *
     * @param implClass a {@link java.lang.String} object.
     * @param field a {@link EarlyFieldRef} object.
     */
    public EarlyFieldSetter(String implClass, EarlyFieldRef field) {
        super(implClass, field);
    }

    /** {@inheritDoc} */
    @Override
    public final void define0(InvokeClassImplBuilder classImplBuilder) {
        Getter<?> getter = classImplBuilder.getGetter(field.getPrev().getUniqueName());
        getter.define(classImplBuilder);

        MethodBody clinit = classImplBuilder.getClinit();
        Type declaredType = Type.getType(field.getDeclaredType());

        this.typeMember = classImplBuilder.defineTypedClass(field);
        this.typeMember.store(clinit, getClassLoadAction(field.getType()));

        // 定义上一层字段的lookup, 必须要用declaredType
        LookupMember lookupMember = classImplBuilder.defineTypedLookup(declaredType.getClassName());
        // init lookup
        lookupMember.staticInit(clinit, getClassLoadAction(declaredType));

        // 定义当前字段的 setter
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(field.getSetterName(), this.methodType);
        // init methodHandle
        initStaticMethodHandle(classImplBuilder, mhMember, lookupMember, declaredType, field.fieldName, Type.getMethodType(Type.VOID_TYPE, field.getType()), field.isStatic());

        // 定义当前字段的 setter
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "").accept(mv -> {
            MethodBody methodBody = new MethodBody(classImplBuilder, mv, methodType);
            VarInst objVar = getter.invoke(methodBody);
            if (!field.isStatic()) {
                objVar.checkNullPointer(methodBody, objVar.getName());
            }

            // mh.invoke(obj)
            VarInst result = field.isStatic() ? mhMember.invokeStatic(methodBody, methodBody.getArg(0)) : mhMember.invokeInstance(methodBody, objVar, methodBody.getArg(0));
            AsmUtil.areturn(mv, Type.VOID_TYPE);
        });
    }

    /** {@inheritDoc} */
    @Override
    protected void initStaticMethodHandle(InvokeClassImplBuilder classImplBuilder, MethodHandleMember mhMember, LookupMember lookupMember, Type ownerType, String fieldName, Type methodType, boolean isStatic) {
        MethodBody clinit = classImplBuilder.getClinit();
        mhMember.store(clinit, new MethodInvokeAction(isStatic ? MethodHolder.LOOKUP_FIND_STATIC_SETTER_METHOD : MethodHolder.LOOKUP_FIND_SETTER_METHOD)
                .setInstance(lookupMember)
                .setArgs(getClassLoadAction(ownerType), LdcLoadAction.of(fieldName), getClassLoadAction(methodType.getArgumentTypes()[0])));
    }
}
