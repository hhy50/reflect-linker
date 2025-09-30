package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.util.TypeUtil;

import java.util.function.BiFunction;

/**
 * The type Early method invoker.
 */
public class EarlyMethodInvoker extends Invoker<EarlyMethodRef> {

    /**
     * 内联方法调用。父类的invoke是调用这个 mh的单独生成的方法
     */
    protected BiFunction<VarInst, ChainAction<VarInst[]>, VarInst> inlineAction;

    /**
     * Instantiates a new Early method invoker.
     *
     * @param methodRef the method ref
     */
    public EarlyMethodInvoker(EarlyMethodRef methodRef) {
        super(methodRef, TypeUtil.appendArgs(methodRef.getMhType(), null, true));
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        MethodBody clinit = classImplBuilder.getClinit();

        // init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(method.getFullName(), method.getLookupClass(), descriptor.getType());
        clinit.append(initStaticMethodHandle(mhMember,
                loadClass(method.getLookupClass()), method.getName(), method.getDeclareType(), method.isStatic()));
        mhMember.setInvokeExact(!method.isInvisible());
        clinit.append(initStaticMethodHandle(mhMember, loadClass(method.getLookupClass()),
                        method.getName(), method.getDeclareType(), method.isStatic()));
        this.inlineAction = (varInst, args) ->
                Actions.newLocalVar(method.isStatic()
                        ? mhMember.invokeStatic(args)
                        : mhMember.invokeInstance(varInst, args));
    }


    @Override
    public ChainAction<VarInst> invoke(ChainAction<VarInst> varInstChain, ChainAction<VarInst[]> argsChainAction) {
        return varInstChain.mapVar(varInst -> {
            // 直接内联调用 methodHandle
            return this.inlineAction.apply(varInst, argsChainAction);
        });
    }

    // @Override
    // public MethodInvokeChainAction invoke(MethodInvokeChainAction varInstChain) {
    //     return varInstChain.invoke((varInst, args) -> {
    //         // 直接内联调用 methodHandle
    //         return this.inlineAction.apply(varInst, args);
    //     });
    // }
}
