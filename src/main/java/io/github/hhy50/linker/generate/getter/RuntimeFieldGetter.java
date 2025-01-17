package io.github.hhy50.linker.generate.getter;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Opcodes;

/**
 * The type Runtime field getter.
 */
public class RuntimeFieldGetter extends Getter<RuntimeFieldRef> {

    /**
     * Instantiates a new Runtime field getter.
     *
     * @param implClass the impl class
     * @param field     the field
     */
    public RuntimeFieldGetter(String implClass, RuntimeFieldRef field) {
        super(implClass, field);
    }

    @Override
    public void define0(InvokeClassImplBuilder classImplBuilder) {
        FieldRef prevField = field.getPrev();
        Getter<?> getter = classImplBuilder.getGetter(prevField.getUniqueName());
        getter.define(classImplBuilder);

        // 保存当前lookup类型
        this.lookupClass = classImplBuilder.defineLookupClass(field.getUniqueName());
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(field.getGetterName(), methodType);

        classImplBuilder
                .defineMethod(Opcodes.ACC_PUBLIC, methodDescriptor.getMethodName(), methodDescriptor.getDesc(), null)
                .intercept(body -> {
                    VarInst objVar = getter.invoke(body);

                    checkLookClass(body, lookupClass, objVar, getter);
                    ClassTypeMember prevLookupClass = getter.getLookupClass();
                    if (prevLookupClass != null) {
                        staticCheckClass(body, lookupClass, prevField.fieldName, prevLookupClass);
                    }
                    checkMethodHandle(body, lookupClass, mhMember, objVar);

                    // mh.invoke(obj)
                    if (field.isDesignateStatic()) {
                        body.append(field.isStatic() ? mhMember.invokeStatic() : mhMember.invokeInstance(objVar));
                    } else {
                        body.append(mhMember.invoke(objVar));
                    }
                    AsmUtil.areturn(body.getWriter(), methodType.getReturnType());
                });
    }
}
