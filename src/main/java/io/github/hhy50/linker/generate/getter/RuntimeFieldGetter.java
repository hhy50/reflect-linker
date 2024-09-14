package io.github.hhy50.linker.generate.getter;

import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.LookupMember;
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

        // 先定义上一层字段的lookup
        LookupMember lookupMember = classImplBuilder.defineRuntimeLookup(field.getPrev());
        // 定义当前字段的mh
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(field.getGetterName(), methodType);

        // 定义当前字段的getter
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "").accept(mv -> {
            MethodBody methodBody = new MethodBody(classImplBuilder, mv, methodType);
            VarInst objVar = getter.invoke(methodBody);

            LookupMember prevLookup = getter.lookupMember;
            if (this.lookupMember != prevLookup) {
                checkLookup(methodBody, lookupMember, mhMember, objVar);
                staticCheckLookup(methodBody, prevLookup, lookupMember, field.getPrev());
            }

            checkMethodHandle(methodBody, lookupMember, mhMember, objVar);

            // mh.invoke(obj)
            VarInst result;
            if (field.isDesignateStatic()) {
                result = field.isStatic() ? mhMember.invokeStatic(methodBody) : mhMember.invokeInstance(methodBody, objVar);
            } else {
                result = mhMember.invoke(methodBody, objVar);
            }
            result.returnThis(methodBody);
        });
    }
}
