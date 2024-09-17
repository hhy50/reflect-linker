package io.github.hhy50.linker.generate.getter;

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
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


/**
 * <p>EarlyFieldGetter class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class EarlyFieldGetter extends Getter<EarlyFieldRef> {

    /**
     * <p>Constructor for EarlyFieldGetter.</p>
     *
     * @param implClass a {@link java.lang.String} object.
     * @param fieldRef  a {@link io.github.hhy50.linker.define.field.EarlyFieldRef} object.
     */
    public EarlyFieldGetter(String implClass, EarlyFieldRef fieldRef) {
        super(implClass, fieldRef);
    }

    /** {@inheritDoc} */
    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        FieldRef prevField = field.getPrev();
        Getter<?> getter = classImplBuilder.getGetter(prevField.getUniqueName());
        getter.define(classImplBuilder);

        Type declaredType = Type.getType(field.getDeclaredType());
        MethodBody clinit = classImplBuilder.getClinit();

        this.lookupClass = classImplBuilder.defineClassTypeMember(field.getUniqueName()+"_lookup");
        this.lookupClass.staticInit(clinit, getClassLoadAction(declaredType));

        // 定义当前字段的getter mh, init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(field.getGetterName(), this.methodType);
        initStaticMethodHandle(clinit, mhMember, lookupClass, field.fieldName, field.getType(), field.isStatic());

        // 定义当前字段的getter
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "")
                .accept(mv -> {
                    MethodBody methodBody = new MethodBody(classImplBuilder, mv, methodType);
                    VarInst result = null;
                    if (!field.isStatic()) {
                        VarInst objVar = getter.invoke(methodBody);
                        objVar.checkNullPointer(methodBody, objVar.getName());
                        result = mhMember.invokeInstance(methodBody, objVar);
                    } else {
                        result = mhMember.invokeStatic(methodBody);
                    }
                    result.returnThis(methodBody);
                });
    }

    /** {@inheritDoc} */
    @Override
    protected void initStaticMethodHandle(MethodBody clinit, MethodHandleMember mhMember, ClassTypeMember lookupClass, String fieldName, Type fieldType, boolean isStatic) {
        VarInst lookupVar = lookupClass.getLookup(clinit);
        MethodInvokeAction findGetter = new MethodInvokeAction(isStatic ? MethodHolder.LOOKUP_FIND_STATIC_GETTER_METHOD : MethodHolder.LOOKUP_FIND_GETTER_METHOD);
        findGetter.setInstance(lookupVar)
                .setArgs(lookupClass, LdcLoadAction.of(fieldName), getClassLoadAction(fieldType));
        mhMember.store(clinit, findGetter);
    }
}
