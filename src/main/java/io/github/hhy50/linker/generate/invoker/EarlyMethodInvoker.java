package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Type;

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

        boolean isStatic = method.isStatic();
        Type mhType = method.getMhType();

        // init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(super.fullName, lookupClass, super.methodType);
        mhMember.setInvokeExact(!method.isInvisible());
        clinit.append(initStaticMethodHandle(mhMember, loadClass(Type.getType(descriptor.getOwner())),
                method.getName(), mhType, isStatic));
        this.inlineAction = (varInst, args) ->
                Actions.newLocalVar(isStatic
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
}
