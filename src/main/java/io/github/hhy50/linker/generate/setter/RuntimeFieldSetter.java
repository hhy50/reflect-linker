package io.github.hhy50.linker.generate.setter;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.getter.Getter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


/**
 * The type Runtime field setter.
 */
public class RuntimeFieldSetter extends Setter<RuntimeFieldRef> {

    /**
     * Instantiates a new Runtime field setter.
     *
     * @param implClass the impl class
     * @param field     the field
     */
    public RuntimeFieldSetter(String implClass, RuntimeFieldRef field) {
        super(implClass, field);
    }

    @Override
    public final void define0(InvokeClassImplBuilder classImplBuilder) {
        FieldRef prevField = field.getPrev();
        Getter<?> getter = classImplBuilder.getGetter(prevField.getUniqueName());
        getter.define(classImplBuilder);

        ClassTypeMember lookupClass = classImplBuilder.defineLookupClass(Opcodes.ACC_PUBLIC, field.getUniqueName());
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(field.getSetterName(), methodType);
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodDescriptor.getMethodName(), methodDescriptor.getDesc(), null).accept(body -> {
            VarInst objVar = getter.invoke(body);

            checkLookClass(body, lookupClass, objVar, getter);
            ClassTypeMember prevLookupClass = getter.getLookupClass();
            if (prevLookupClass != null) {
                staticCheckClass(body, lookupClass, prevField.fieldName, prevLookupClass);
            }
            checkMethodHandle(body, lookupClass, mhMember, objVar);

            // mh.invoke(obj, fieldValue)
            if (field.isDesignateStatic()) {
                body.append(field.isStatic() ? mhMember.invokeStatic() : mhMember.invokeInstance(objVar));
            } else {
                body.append(mhMember.invoke(objVar, body.getArg(0)));
            }
            AsmUtil.areturn(body.getWriter(), Type.VOID_TYPE);
        });
    }
}
