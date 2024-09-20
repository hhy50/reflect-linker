package io.github.hhy50.linker.generate.setter;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.getter.Getter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


/**
 * <p>RuntimeFieldSetter class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class RuntimeFieldSetter extends Setter<RuntimeFieldRef> {

    /**
     * <p>Constructor for RuntimeFieldSetter.</p>
     *
     * @param implClass a {@link java.lang.String} object.
     * @param field     a {@link io.github.hhy50.linker.define.field.RuntimeFieldRef} object.
     */
    public RuntimeFieldSetter(String implClass, RuntimeFieldRef field) {
        super(implClass, field);
    }

    /** {@inheritDoc} */
    @Override
    public final void define0(InvokeClassImplBuilder classImplBuilder) {
        FieldRef prevField = field.getPrev();
        Getter<?> getter = classImplBuilder.getGetter(prevField.getUniqueName());
        getter.define(classImplBuilder);

        ClassTypeMember lookupClass = classImplBuilder.defineLookupClass(Opcodes.ACC_PUBLIC, field.getUniqueName());
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(field.getSetterName(), methodType);
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "").accept(mv -> {
            MethodBody body = new MethodBody(classImplBuilder, mv, methodType);
            VarInst objVar = getter.invoke(body);

            checkLookClass(body, lookupClass, objVar, getter);
            ClassTypeMember prevLookupClass = getter.getLookupClass();
            if (prevLookupClass != null) {
                staticCheckClass(body, lookupClass, prevField.fieldName, prevLookupClass);
            }
            checkMethodHandle(body, lookupClass, mhMember, objVar);

            // mh.invoke(obj, fieldValue)
            if (field.isDesignateStatic()) {
                VarInst vold = field.isStatic() ? mhMember.invokeStatic(body, body.getArg(0)) : mhMember.invokeInstance(body, objVar, body.getArg(0));
            } else {
                mhMember.invoke(body, objVar, body.getArg(0));
            }
            AsmUtil.areturn(mv, Type.VOID_TYPE);
        });
    }
}
