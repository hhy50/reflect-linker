package io.github.hhy50.linker.generate.getter;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


/**
 * The type Early field getter.
 */
public class EarlyFieldGetter extends Getter<EarlyFieldRef> {

    /**
     * Instantiates a new Early field getter.
     *
     * @param implClass the impl class
     * @param fieldRef  the field ref
     */
    public EarlyFieldGetter(String implClass, EarlyFieldRef fieldRef) {
        super(implClass, fieldRef);
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        FieldRef prevField = field.getPrev();
        Getter<?> getter = classImplBuilder.getGetter(prevField.getUniqueName());
        getter.define(classImplBuilder);

        MethodBody clinit = classImplBuilder.getClinit();

        // 定义当前字段的getter mh, init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(field.getGetterName(), null, descriptor.getType());
        initStaticMethodHandle(clinit, mhMember, loadClass(field.getDeclaredType()), field.fieldName, field.getType(), field.isStatic());

        // 定义当前字段的getter
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, descriptor.getMethodName(), descriptor.getType(), null)
                .intercept((field.isStatic()
                        ? mhMember.invokeStatic()
                        : ChainAction.of(getter::invoke).peek(VarInst::checkNullPointer).then(varInst -> mhMember.invokeInstance(varInst)))
                        .andThen(Actions.areturn(descriptor.getReturnType()))
                );
    }

    @Override
    protected void initStaticMethodHandle(MethodBody clinit, MethodHandleMember mhMember, ClassLoadAction lookupClass, String fieldName, Type fieldType, boolean isStatic) {
        MethodInvokeAction findGetter = new MethodInvokeAction(isStatic ? MethodDescriptor.LOOKUP_FINDSTATICGETTER : MethodDescriptor.LOOKUP_FINDGETTER);
        findGetter.setInstance(lookupClass.getLookup())
                .setArgs(lookupClass, LdcLoadAction.of(fieldName), loadClass(fieldType));
        mhMember.store(clinit, findGetter);
    }
}
