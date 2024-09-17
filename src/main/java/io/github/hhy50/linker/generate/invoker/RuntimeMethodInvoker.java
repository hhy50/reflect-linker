package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.method.RuntimeMethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.getter.Getter;
import org.objectweb.asm.Opcodes;


/**
 * <p>RuntimeMethodInvoker class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class RuntimeMethodInvoker extends Invoker<RuntimeMethodRef> {

    /**
     * <p>Constructor for RuntimeMethodInvoker.</p>
     *
     * @param implClass a {@link java.lang.String} object.
     * @param methodRef a {@link io.github.hhy50.linker.define.method.RuntimeMethodRef} object.
     */
    public RuntimeMethodInvoker(String implClass, RuntimeMethodRef methodRef) {
        super(implClass, methodRef, methodRef.getMethodType());
    }

    /** {@inheritDoc} */
    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        FieldRef owner = method.getOwner();
        Getter<?> ownerGetter = classImplBuilder.defineGetter(owner.getUniqueName(), owner);
        ownerGetter.define(classImplBuilder);
        
        ClassTypeMember lookupClass = classImplBuilder.defineClassTypeMember(Opcodes.ACC_PUBLIC, method.getFullName()+"_lookup");
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(method.getInvokerName(), methodType);

        classImplBuilder
                .defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "")
                .accept(mv -> {
                    MethodBody body = new MethodBody(classImplBuilder, mv, methodType);
                    VarInst objVar = ownerGetter.invoke(body);

                    ClassTypeMember prevLookupClass = ownerGetter.getLookupClass();
                    if (lookupClass != prevLookupClass) {
                        checkLookClass(body, lookupClass, objVar);
                        staticCheckClass(body, lookupClass, owner.fieldName, prevLookupClass);
                    }
                    checkMethodHandle(body, lookupClass, mhMember, objVar);

                    // mh.invoke(obj)
                    VarInst result;
                    if (method.isDesignateStatic()) {
                        result = method.isStatic() ? mhMember.invokeStatic(body, body.getArgs()) : mhMember.invokeInstance(body, objVar, body.getArgs());
                    } else {
                        result = mhMember.invoke(body, objVar, body.getArgs());
                    }
                    if (result != null) {
                        result.load(body);
                    }
                    AsmUtil.areturn(mv, methodType.getReturnType());
                });
    }
}
