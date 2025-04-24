package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.method.RuntimeMethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.getter.Getter;
import org.objectweb.asm.Opcodes;


/**
 * The type Runtime method invoker.
 */
public class RuntimeMethodInvoker extends Invoker<RuntimeMethodRef> {

    /**
     * Instantiates a new Runtime method invoker.
     *
     * @param implClass the impl class
     * @param methodRef the method ref
     */
    public RuntimeMethodInvoker(String implClass, RuntimeMethodRef methodRef) {
        super(implClass, methodRef, methodRef.getMethodType());
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        FieldRef owner = method.getOwner();
        Getter<?> ownerGetter = classImplBuilder.defineGetter(owner.getUniqueName(), owner);
        ownerGetter.define(classImplBuilder);
        
        ClassTypeMember lookupClass = classImplBuilder.defineLookupClass(method.getFullName());
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(method.getInvokerName(), descriptor.getType());

        classImplBuilder
                .defineMethod(Opcodes.ACC_PUBLIC, descriptor.getMethodName(), descriptor.getType(), null)
                .intercept(body -> {
                    VarInst objVar = ownerGetter.invoke(body);

                    checkLookClass(body, lookupClass, objVar, ownerGetter);
                    ClassTypeMember prevLookupClass = ownerGetter.getLookupClass();
                    if (prevLookupClass != null) {
                        staticCheckClass(body, lookupClass, owner.fieldName, prevLookupClass);
                    }
                    checkMethodHandle(body, lookupClass, mhMember, objVar);

                    // mh.invoke(obj)
                    if (method.isDesignateStatic()) {
                        body.append(method.isStatic() ? mhMember.invokeStatic(body.getArgs()) : mhMember.invokeInstance(objVar, body.getArgs()));
                    } else {
                        body.append(mhMember.invokeOfNull(objVar, body.getArgs()));
                    }
                    AsmUtil.areturn(body.getWriter(), descriptor.getReturnType());
                });
    }
}
