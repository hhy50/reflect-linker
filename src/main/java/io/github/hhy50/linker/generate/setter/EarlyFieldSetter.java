package io.github.hhy50.linker.generate.setter;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.utils.Args;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.getter.Getter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * The type Early field setter.
 */
public class EarlyFieldSetter extends Setter<EarlyFieldRef> {

    /**
     * Instantiates a new Early field setter.
     *
     * @param implClass the impl class
     * @param field     the field
     */
    public EarlyFieldSetter(String implClass, EarlyFieldRef field) {
        super(implClass, field);
    }

    @Override
    public final void define0(InvokeClassImplBuilder classImplBuilder) {
        FieldRef prevField = field.getPrev();
        Getter<?> getter = classImplBuilder.getGetter(prevField.getUniqueName());
        getter.define(classImplBuilder);

        MethodBody clinit = classImplBuilder.getClinit();

        // init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(field.getSetterName(), null, this.methodType);
        initStaticMethodHandle(clinit, mhMember, loadClass(field.getDeclaredType()), field.fieldName, field.getType(), field.isStatic());

        // 定义当前字段的 setter
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodDescriptor.getMethodName(), methodDescriptor.getDesc(), null)
                .intercept((field.isStatic()
                        ? mhMember.invokeStatic(Args.loadArgs())
                        : ChainAction.of(getter::invoke).peek(VarInst::checkNullPointer).then(varInst -> mhMember.invokeInstance(varInst, Args.loadArgs())))
                        .andThen(Actions.vreturn())
                );
    }

    @Override
    protected void initStaticMethodHandle(MethodBody clinit, MethodHandleMember mhMember, ClassLoadAction lookupClass, String fieldName, Type fieldType, boolean isStatic) {
        mhMember.store(clinit, new MethodInvokeAction(isStatic ? MethodDescriptor.LOOKUP_FINDSTATICSETTER : MethodDescriptor.LOOKUP_FINDSETTER)
                .setInstance(lookupClass.getLookup())
                .setArgs(lookupClass, LdcLoadAction.of(fieldName), loadClass(fieldType)));
    }
}
