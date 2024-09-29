package io.github.hhy50.linker.generate.getter;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.LdcLoadAction;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
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

        this.lookupClass = classImplBuilder.defineLookupClass(field.getUniqueName());
        this.lookupClass.staticInit(clinit, getClassLoadAction(field.getDeclaredType()));

        // 定义当前字段的getter mh, init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(field.getGetterName(), this.methodType);
        initStaticMethodHandle(clinit, mhMember, lookupClass, field.fieldName, field.getType(), field.isStatic());

        // 定义当前字段的getter
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodDescriptor.getMethodName(), methodDescriptor.getDesc(), null)
                .intercept((field.isStatic()
                        ? mhMember.invokeStatic()
                        : getter.invoke().peek(VarInst::checkNullPointer).then(varInst -> mhMember.invokeInstance(varInst)))
                        .thenReturn()
                );
//                .accept(body -> {
//                    if (!field.isStatic()) {
//                        VarInst objVar = getter.invoke(body);
//                        objVar.checkNullPointer(objVar.getName());
//                        body.append(mhMember.invokeInstance(objVar));
//                    } else {
//                        body.append(mhMember.invokeStatic());
//                    }
//                    AsmUtil.areturn(body.getWriter(), methodType.getReturnType());
//                });
    }

    @Override
    protected void initStaticMethodHandle(MethodBody clinit, MethodHandleMember mhMember, ClassTypeMember lookupClass, String fieldName, Type fieldType, boolean isStatic) {
        VarInst lookupVar = lookupClass.getLookup(clinit);
        MethodInvokeAction findGetter = new MethodInvokeAction(isStatic ? MethodDescriptor.LOOKUP_FIND_STATIC_GETTER_METHOD : MethodDescriptor.LOOKUP_FIND_GETTER_METHOD);
        findGetter.setInstance(lookupVar)
                .setArgs(lookupClass, LdcLoadAction.of(fieldName), getClassLoadAction(fieldType));
        mhMember.store(clinit, findGetter);
    }
}
