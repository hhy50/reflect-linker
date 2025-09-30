package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.annotations.Autolink;
import io.github.hhy50.linker.define.method.RuntimeMethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.utils.Args;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


/**
 * The type Runtime method invoker.
 */
public class RuntimeMethodInvoker extends Invoker<RuntimeMethodRef> {

    /**
     * Instantiates a new Runtime method invoker.
     *
     * @param methodRef the method ref
     */
    public RuntimeMethodInvoker(RuntimeMethodRef methodRef) {
        super(methodRef, TypeUtil.appendArgs(methodRef.getMhType(), ObjectVar.TYPE, true));
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        boolean autolink = method.isAutolink();
        Type mhType = descriptor.getType();
        Action args = autolink ? Actions.asArray(ObjectVar.TYPE, Args.loadArgsIgnore0()) : Args.loadArgsIgnore0();
        if (autolink) {
            // 因为是根据形参寻找方法，但是形参是链接器，所以找不到具体方法，查找逻辑在io.github.hhy50.linker.runtime.Runtime.findMethod
            // 约定将参数0设置为Autolink，以保证使用实参来查找方法
            mhType = Type.getMethodType(descriptor.getReturnType(), Type.getType(Object[].class));
            method.setArgsType(new Type[]{Type.getType(Autolink.class)});
        }
        ClassTypeMember lookupClass = classImplBuilder.defineLookupClass(method.getFullName());
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(method.getFullName(), mhType);

        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, descriptor.getMethodName(), descriptor.getType(), null)
                .intercept(ChainAction.of(() -> Args.of(0))
                                .then(ownerVar -> checkLookClass(lookupClass, ownerVar, null))
                                .then(ownerVar -> {
//                                    ClassTypeMember prevLookupClass = ownerGetter.lookupClass;
//                                    if (prevLookupClass != null) {
//                                        return staticCheckClass(lookupClass, owner.fieldName, prevLookupClass);
//                                    }
                                    return null;
                                })
                                .then(ownerVar -> checkMethodHandle(lookupClass, mhMember, ownerVar))
                                .map(method.isDesignateStatic() ?
                                        (method.isStatic() ? ownerVar -> mhMember.invokeStatic(args)
                                                : ownerVar -> mhMember.invokeInstance(ownerVar, args))
                                        : ownerVar -> mhMember.invokeOfNull(ownerVar, args)
                                ),
                        Actions.areturn(descriptor.getReturnType()));
    }

    @Override
    public ChainAction<VarInst> invoke(ChainAction<VarInst> varInstChain, ChainAction<VarInst[]> argsChainAction) {
        return varInstChain.then(VarInst::checkNullPointer)
                .mapVar(varInst -> {
                    return new SmartMethodInvokeAction(descriptor)
                            .setInstance(LoadAction.LOAD0)
                            .setArgs(varInst, argsChainAction);
                });
    }
}
