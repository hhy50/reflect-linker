package io.github.hhy50.linker.generate.getter;

import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.entity.MethodHolder;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.LookupMember;
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
        Getter<?> getter = classImplBuilder.getGetter(field.getPrev().getUniqueName());
        getter.define(classImplBuilder);

        MethodBody clinit = classImplBuilder.getClinit();
        Type declaredType = Type.getType(field.getDeclaredType());

        // 保存当前字段类型
        this.typeMember = classImplBuilder.defineTypedClass(field);
        this.typeMember.store(clinit, getClassLoadAction(field.getType()));

        // 上层字段的lookup
        VarInst lookup = getter.getTypeMember().getLookup(clinit);

        // 定义当前字段的getter mh, init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(field.getGetterName(), this.methodType);
        initStaticMethodHandle(clinit, mhMember, lookup, declaredType, field.fieldName, Type.getMethodType(field.getType()), field.isStatic());

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
    protected void initStaticMethodHandle(MethodBody clinit, MethodHandleMember mhMember, VarInst lookupVar, Type ownerType, String fieldName, Type methodType, boolean isStatic) {
        // mh = LookupVar.FINDGetter(ArrayList.class, "elementData", Object[].class);
        MethodInvokeAction findGetter = new MethodInvokeAction(isStatic ? MethodHolder.LOOKUP_FIND_STATIC_GETTER_METHOD : MethodHolder.LOOKUP_FIND_GETTER_METHOD);
        findGetter.setInstance(lookupVar)
                .setArgs(getClassLoadAction(ownerType), LdcLoadAction.of(fieldName), getClassLoadAction(methodType.getReturnType()));
        mhMember.store(clinit, findGetter);
    }
}
