package io.github.hhy.linker.generate.setter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.define.field.RuntimeFieldRef;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import io.github.hhy.linker.generate.getter.Getter;
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
     * @param field a {@link io.github.hhy.linker.define.field.RuntimeFieldRef} object.
     */
    public RuntimeFieldSetter(String implClass, RuntimeFieldRef field) {
        super(implClass, field);
    }

    /** {@inheritDoc} */
    @Override
    public final void define0(InvokeClassImplBuilder classImplBuilder) {
        Getter<?> getter = classImplBuilder.getGetter(field.getPrev().getUniqueName());
        getter.define(classImplBuilder);

        // 先定义上一层字段的lookup
        LookupMember lookupMember = classImplBuilder.defineRuntimeLookup(field.getPrev());
        // 定义当前字段的mh
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(field.getSetterName(), methodType);
        // 定义当前字段的getter
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "").accept(mv -> {
            MethodBody methodBody = new MethodBody(classImplBuilder, mv, methodType);
            VarInst objVar = getter.invoke(methodBody);

            if (!getter.isTargetGetter()) {
                // 校验lookup和mh
                LookupMember prevLookup = getter.getLookupMember();
                staticCheckLookup(methodBody, prevLookup, lookupMember, objVar, field.getPrev());
                checkLookup(methodBody, lookupMember, mhMember, objVar);
            }
            checkMethodHandle(methodBody, lookupMember, mhMember, objVar);

            // mh.invoke(obj, fieldValue)
            if (field.isDesignateStatic()) {
                VarInst vold = field.isStatic() ? mhMember.invokeStatic(methodBody, methodBody.getArg(0)) : mhMember.invokeInstance(methodBody, objVar, methodBody.getArg(0));
            } else {
                mhMember.invoke(methodBody, objVar, methodBody.getArg(0));
            }
            AsmUtil.areturn(mv, Type.VOID_TYPE);
        });
    }
}
