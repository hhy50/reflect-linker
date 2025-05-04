package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.annotations.Autolink;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.method.RuntimeMethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.utils.Args;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.getter.Getter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


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
        boolean autolink = method.isAutolink();
        FieldRef owner = method.getOwner();
        Getter ownerGetter = classImplBuilder.defineGetter(owner.getUniqueName(), owner);
        ownerGetter.define(classImplBuilder);

        Type mhType = descriptor.getType();
        Action args = autolink ? Actions.asArray(ObjectVar.TYPE, MethodBody::getArgs) : Args.loadArgs();
        if (autolink) {
            mhType = Type.getMethodType(descriptor.getReturnType(), Type.getType(Object[].class));
            method.setArgsType(new Type[]{Type.getType(Autolink.class)});
        }
        ClassTypeMember lookupClass = classImplBuilder.defineLookupClass(method.getUniqueName());
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(method.getInvokerName(), mhType);

        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, descriptor.getMethodName(), descriptor.getType(), null)
                .intercept(ChainAction.of(ownerGetter::invoke)
                                .peek((body, ownerVar) -> checkLookClass(body, lookupClass, ownerVar, ownerGetter))
                                .peek((body, ownerVar) -> {
                                    ClassTypeMember prevLookupClass = ownerGetter.lookupClass;
                                    if (prevLookupClass != null) {
                                        staticCheckClass(body, lookupClass, owner.fieldName, prevLookupClass);
                                    }
                                })
                                .peek((body, ownerVar) -> checkMethodHandle(body, lookupClass, mhMember, ownerVar))
                                .then(method.isDesignateStatic() ?
                                        (method.isStatic() ? ownerVar -> mhMember.invokeStatic(args)
                                                : ownerVar -> mhMember.invokeInstance(ownerVar, args))
                                        : ownerVar -> mhMember.invokeOfNull(ownerVar, args)
                                ),
                        Actions.areturn(descriptor.getReturnType()));
    }
}
