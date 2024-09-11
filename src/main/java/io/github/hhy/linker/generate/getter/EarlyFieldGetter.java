package io.github.hhy.linker.generate.getter;

import io.github.hhy.linker.define.field.EarlyFieldRef;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.action.LdcLoadAction;
import io.github.hhy.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static io.github.hhy.linker.entity.MethodHolder.LOOKUP_FIND_GETTER_METHOD;
import static io.github.hhy.linker.entity.MethodHolder.LOOKUP_FIND_STATIC_GETTER_METHOD;


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
     * @param fieldRef a {@link io.github.hhy.linker.define.field.EarlyFieldRef} object.
     */
    public EarlyFieldGetter(String implClass, EarlyFieldRef fieldRef) {
        super(implClass, fieldRef);
    }

    /** {@inheritDoc} */
    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        Getter<?> getter = classImplBuilder.getGetter(field.getPrev().getUniqueName());
        getter.define(classImplBuilder);

        MethodBody clinit = classImplBuilder.getClinit();
        Type declaredType = Type.getType(field.getDeclaredType());

        // 定义上一层字段的lookup, 必须要用declaredType
        this.lookupMember = classImplBuilder.defineTypedLookup(declaredType.getClassName());
        // init lookup
        this.lookupMember.staticInit(clinit, getClassLoadAction(declaredType));

        // 定义当前字段的getter mh
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(field.getGetterName(), this.methodType);
        // init methodHandle
        initStaticMethodHandle(classImplBuilder, mhMember, lookupMember, declaredType, field.fieldName, Type.getMethodType(field.getType()), field.isStatic());
        // 定义当前字段的getter
        classImplBuilder
                .defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "")
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

    /** {@inheritDoc} */
    @Override
    protected void initStaticMethodHandle(InvokeClassImplBuilder classImplBuilder, MethodHandleMember mhMember, LookupMember lookupMember,
                                          Type ownerType, String fieldName, Type methodType, boolean isStatic) {
        // mh = lookup.findGetter(ArrayList.class, "elementData", Object[].class);
        MethodInvokeAction findGetter = new MethodInvokeAction(isStatic ? LOOKUP_FIND_STATIC_GETTER_METHOD : LOOKUP_FIND_GETTER_METHOD);
        findGetter.setInstance(lookupMember).setArgs(getClassLoadAction(ownerType), LdcLoadAction.of(fieldName), getClassLoadAction(methodType.getReturnType()));
        mhMember.store(classImplBuilder.getClinit(), findGetter);
    }
}
