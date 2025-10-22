package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.annotations.Autolink;
import io.github.hhy50.linker.define.SmartMethodDescriptor;
import io.github.hhy50.linker.define.method.RuntimeMethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.utils.Args;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


/**
 * The type Runtime method invoker.
 */
public class RuntimeMethodInvoker extends Invoker<RuntimeMethodRef> {

   private boolean autolink;

    private Type[] argsType;

    private String fullName;

    private Boolean isDesignateStatic;

    /**
     * Instantiates a new Runtime method invoker.
     *
     * @param methodRef the method ref
     */
    public RuntimeMethodInvoker(RuntimeMethodRef methodRef) {
        super(methodRef.getName(), methodRef.getMhType());
        this.autolink = methodRef.isAutolink();
        this.argsType = methodRef.getArgsType();
        this.fullName = methodRef.getFullName().replace('.', '_');
        this.isDesignateStatic = methodRef.isDesignateStatic();
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        Type mhType = super.lookupMhType;
        Action args = autolink ? Actions.asArray(ObjectVar.TYPE, Args.loadArgsIgnore0()) : Args.loadArgsIgnore0();
        if (autolink) {
            // 因为是根据形参寻找方法，但是形参是链接器，所以找不到具体方法，查找逻辑在io.github.hhy50.linker.runtime.Runtime.findMethod
            // 约定将参数0设置为Autolink，以保证使用实参来查找方法
            mhType = Type.getMethodType(mhType.getReturnType(), Type.getType(Object[].class));
            this.argsType = new Type[]{Type.getType(Autolink.class)};
        }
        ClassTypeMember lookupClass = classImplBuilder.defineLookupClass(this.fullName);
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(this.fullName, mhType);

        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, "invoke_"+this.fullName, super.lookupMhType, null)
                .intercept(ChainAction.of(() -> Args.of(0))
                                .then(ownerVar -> checkLookClass(lookupClass, ownerVar, null))
                                .then(ownerVar -> {
//                                    ClassTypeMember prevLookupClass = ownerGetter.lookupClass;
//                                    if (prevLookupClass != null) {
//                                        return staticCheckClass(lookupClass, owner.fieldName, prevLookupClass);
//                                    }
                                    return null;
                                })
                                .then(ownerVar -> checkMethodHandle(lookupClass, mhMember))
                                .map(isDesignateStatic != null ?
                                        (isDesignateStatic ? ownerVar -> mhMember.invokeStatic(args)
                                                : ownerVar -> mhMember.invokeInstance(ownerVar, args))
                                        : ownerVar -> mhMember.invokeOfNull(ownerVar, args)
                                ),
                        Actions.areturn(super.lookupMhType.getReturnType()));
    }

    @Override
    protected Action initRuntimeMethodHandle(MethodHandleMember mhMember, ClassTypeMember lookupClass) {
        return super.initRuntimeMethodHandle(mhMember, lookupClass);
    }

    @Override
    public ChainAction<VarInst> invoke(ChainAction<VarInst> varInstChain, ChainAction<VarInst[]> argsChainAction) {
        return varInstChain.then(VarInst::checkNullPointer)
                .mapVar(varInst -> {
                    return new SmartMethodInvokeAction(new SmartMethodDescriptor("invoke_"+this.fullName, super.lookupMhType))
                            .setInstance(LoadAction.LOAD0)
                            .setArgs(varInst, argsChainAction);
                });
    }
}
