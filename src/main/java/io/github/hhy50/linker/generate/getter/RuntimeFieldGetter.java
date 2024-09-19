package io.github.hhy50.linker.generate.getter;

import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
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
     * @param field     a {@link io.github.hhy50.linker.define.field.RuntimeFieldRef} object.
     */
    public RuntimeFieldGetter(String implClass, RuntimeFieldRef field) {
        super(implClass, field);
    }

    /** {@inheritDoc} */
    @Override
    public void define0(InvokeClassImplBuilder classImplBuilder) {
        FieldRef prevField = field.getPrev();
        Getter<?> getter = classImplBuilder.getGetter(prevField.getUniqueName());
        getter.define(classImplBuilder);

        // 保存当前lookup类型
        this.lookupClass = classImplBuilder.defineClassTypeMember(Opcodes.ACC_PUBLIC, field.getUniqueName()+"_lookup");
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(field.getGetterName(), methodType);

        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "").accept(mv -> {
            MethodBody body = new MethodBody(classImplBuilder, mv, methodType);
            VarInst objVar = getter.invoke(body);

            checkLookClass(body, lookupClass, objVar, getter);
            ClassTypeMember prevLookupClass = getter.getLookupClass();
            if (prevLookupClass != null) {
                staticCheckClass(body, lookupClass, prevField.fieldName, prevLookupClass);
            }
            checkMethodHandle(body, lookupClass, mhMember, objVar);

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
