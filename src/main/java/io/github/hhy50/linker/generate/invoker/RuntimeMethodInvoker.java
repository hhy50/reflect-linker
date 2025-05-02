package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.method.RuntimeMethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.utils.Args;
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
        Getter ownerGetter = classImplBuilder.defineGetter(owner.getUniqueName(), owner);
        ownerGetter.define(classImplBuilder);

        ClassTypeMember lookupClass = classImplBuilder.defineLookupClass(method.getUniqueName());
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(method.getInvokerName(), descriptor.getType());

        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, descriptor.getMethodName(), descriptor.getType(), null)
                .intercept(ChainAction.of(ownerGetter::invoke)
                                .peek((body, ownerVar) -> checkLookClass(body, lookupClass, ownerVar, ownerGetter))
                                .peek((body, ownerVar) -> {
                                    ClassTypeMember prevLookupClass = ownerGetter.getLookupClass();
                                    if (prevLookupClass != null) {
                                        staticCheckClass(body, lookupClass, owner.fieldName, prevLookupClass);
                                    }
                                })
                                .peek((body, ownerVar) -> checkMethodHandle(body, lookupClass, mhMember, ownerVar))
                                .then(method.isDesignateStatic() ?
                                        (method.isStatic() ? ownerVar -> mhMember.invokeStatic(Args.loadArgs())
                                                : ownerVar -> mhMember.invokeInstance(ownerVar, Args.loadArgs()))
                                        : ownerVar -> mhMember.invokeOfNull(ownerVar, Args.loadArgs())
                                ),
                        Actions.areturn(descriptor.getReturnType()));
    }
}
