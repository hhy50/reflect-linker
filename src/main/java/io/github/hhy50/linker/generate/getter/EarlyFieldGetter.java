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
     * @param fieldRef  a {@link EarlyFieldRef} object.
     */
    public EarlyFieldGetter(String implClass, EarlyFieldRef fieldRef) {
        super(implClass, fieldRef);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        FieldRef prevField = field.getPrev();
        Getter<?> getter = classImplBuilder.getGetter(prevField.getUniqueName());
        getter.define(classImplBuilder);

        MethodBody clinit = classImplBuilder.getClinit();

        // 保存上一级的字段类型
        this.ownerType = classImplBuilder.defineClassTypeMember(prevField.getUniqueName(), prevField.getType());
        this.ownerType.store(clinit, getClassLoadAction(prevField.getType()));

        // 定义当前字段的getter mh, init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(field.getGetterName(), this.methodType);
        initStaticMethodHandle(clinit, mhMember, ownerType, field.fieldName, field.getType(), field.isStatic());
        // 定义当前字段的getter
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "")
                .accept(mv -> {
                    MethodBody methodBody = new MethodBody(classImplBuilder, mv, methodType);
                    VarInst objVar = getter.invoke(methodBody);
                    if (!field.isStatic()) {
                        objVar.checkNullPointer(methodBody, objVar.getName());
                    }

                    // mh.invoke(obj)
                    VarInst result = field.isStatic() ? mhMember.invokeStatic(methodBody) : mhMember.invokeInstance(methodBody, objVar);
                    result.returnThis(methodBody);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initStaticMethodHandle(MethodBody clinit, MethodHandleMember mhMember, ClassTypeMember ownerClass, String fieldName, Type fieldType, boolean isStatic) {
        // mh = lookupVar.findGetter(ArrayList.class, "elementData", Object[].class);
        VarInst lookupVar = ownerClass.getLookup(clinit);
        MethodInvokeAction findGetter = new MethodInvokeAction(isStatic ? MethodHolder.LOOKUP_FIND_STATIC_GETTER_METHOD : MethodHolder.LOOKUP_FIND_GETTER_METHOD);
        findGetter.setInstance(lookupVar)
                .setArgs(ownerClass, LdcLoadAction.of(fieldName), getClassLoadAction(fieldType));
        mhMember.store(clinit, findGetter);
    }
}
