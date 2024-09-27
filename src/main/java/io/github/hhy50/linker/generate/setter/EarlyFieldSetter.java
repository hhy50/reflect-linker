package io.github.hhy50.linker.generate.setter;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.entity.MethodDescriptor;
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

        ClassTypeMember lookupClass = classImplBuilder.defineLookupClass(field.getUniqueName());
        lookupClass.staticInit(clinit, getClassLoadAction(field.getDeclaredType()));

        // init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(field.getSetterName(), this.methodType);
        initStaticMethodHandle(clinit, mhMember, lookupClass, field.fieldName, field.getType(), field.isStatic());

        // 定义当前字段的 setter
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodDescriptor.getMethodName(), methodDescriptor.getDesc(), null).accept(body -> {
            if (!field.isStatic()) {
                VarInst objVar = getter.invoke(body);
                objVar.checkNullPointer(objVar.getName());
                body.append(mhMember.invokeInstance(objVar, body.getArg(0)));
            } else {
                body.append(mhMember.invokeStatic(body.getArg(0)));
            }
            AsmUtil.areturn(body.getWriter(), Type.VOID_TYPE);
        });
    }

    @Override
    protected void initStaticMethodHandle(MethodBody clinit, MethodHandleMember mhMember, ClassTypeMember lookupClass, String fieldName, Type fieldType, boolean isStatic) {
        VarInst lookupVar = lookupClass.getLookup(clinit);
        mhMember.store(clinit, new MethodInvokeAction(isStatic ? MethodDescriptor.LOOKUP_FIND_STATIC_SETTER_METHOD : MethodDescriptor.LOOKUP_FIND_SETTER_METHOD)
                .setInstance(lookupVar)
                .setArgs(lookupClass, LdcLoadAction.of(fieldName), getClassLoadAction(fieldType)));
    }
}
