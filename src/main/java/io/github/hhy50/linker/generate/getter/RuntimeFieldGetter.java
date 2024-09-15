package io.github.hhy50.linker.generate.getter;

import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Opcodes;

/**
 * <p>RuntimeFieldGetter class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class RuntimeFieldGetter extends Getter<RuntimeFieldRef> {

    /**
     * <p>Constructor for RuntimeFieldGetter.</p>
     *
     * @param implClass a {@link java.lang.String} object.
     * @param field a {@link RuntimeFieldRef} object.
     */
    public RuntimeFieldGetter(String implClass, RuntimeFieldRef field) {
        super(implClass, field);
    }

    /** {@inheritDoc} */
    @Override
    public void define0(InvokeClassImplBuilder classImplBuilder) {
        Getter<?> getter = classImplBuilder.getGetter(field.getPrev().getUniqueName());
        getter.define(classImplBuilder);

        //this.typeMember.store(clinit, getClassLoadAction(field.getType()));

        // 保存当前字段类型
        this.ownerType = classImplBuilder.defineClassTypeMember(field.getUniqueName(), null);
        // 定义当前字段的mh
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(field.getGetterName(), methodType);

        // 定义当前字段的getter
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "").accept(mv -> {
            MethodBody body = new MethodBody(classImplBuilder, mv, methodType);
            VarInst objVar = getter.invoke(body);

            this.ownerType.checkClass(body, objVar, getter.getOwnerType());
            VarInst lookup = this.ownerType.getLookup(body);
            checkMethodHandle(body, lookup, mhMember, objVar);

            // mh.invoke(obj)
            VarInst result;
            if (field.isDesignateStatic()) {
                result = field.isStatic() ? mhMember.invokeStatic(body) : mhMember.invokeInstance(body, objVar);
            } else {
                result = mhMember.invoke(body, objVar);
            }
            result.returnThis(body);
        });
    }
}
