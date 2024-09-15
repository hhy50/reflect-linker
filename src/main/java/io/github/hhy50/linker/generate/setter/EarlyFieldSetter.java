package io.github.hhy50.linker.generate.setter;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.entity.MethodHolder;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
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
        FieldRef prevField = field.getPrev();
        Getter<?> getter = classImplBuilder.getGetter(prevField.getUniqueName());
        getter.define(classImplBuilder);

        MethodBody clinit = classImplBuilder.getClinit();
        ClassTypeMember ownerType = classImplBuilder.defineClassTypeMember(prevField.getUniqueName(), prevField.getType());
        ownerType.store(clinit, getClassLoadAction(prevField.getType()));

        // 定义当前字段的 setter
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(field.getSetterName(), this.methodType);
        // init methodHandle
        initStaticMethodHandle(clinit, mhMember, ownerType, field.fieldName, field.getType(), field.isStatic());

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
    protected void initStaticMethodHandle(MethodBody clinit, MethodHandleMember mhMember, ClassTypeMember ownerClass, String fieldName, Type fieldType, boolean isStatic) {
        VarInst lookupVar = ownerClass.getLookup(clinit);
        mhMember.store(clinit, new MethodInvokeAction(isStatic ? MethodHolder.LOOKUP_FIND_STATIC_SETTER_METHOD : MethodHolder.LOOKUP_FIND_SETTER_METHOD)
                .setInstance(lookupVar)
                .setArgs(ownerClass, LdcLoadAction.of(fieldName), getClassLoadAction(fieldType)));
    }
}
