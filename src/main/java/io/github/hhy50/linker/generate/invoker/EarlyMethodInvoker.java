package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.getter.Getter;
import io.github.hhy50.linker.util.TypeUtil;

import java.util.function.BiFunction;

/**
 * The type Early method invoker.
 */
public class EarlyMethodInvoker extends Invoker<EarlyMethodRef> {

    /**
     * 内联方法调用。父类的invoke是调用这个 mh的单独生成的方法
     */
    protected BiFunction<VarInst, Action[], VarInst> inlineAction;

    /**
     * Instantiates a new Early method invoker.
     *
     * @param methodRef the method ref
     */
    public EarlyMethodInvoker(EarlyMethodRef methodRef) {
        super(methodRef, TypeUtil.appendArgs(methodRef.getMethodType(), null, true));
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        FieldRef owner = method.getOwner();
        Getter getter = classImplBuilder.getGetter(owner);
        getter.define(classImplBuilder);

        MethodBody clinit = classImplBuilder.getClinit();

        // init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(method.getInvokerName(), method.getLookupClass(), descriptor.getType());
        clinit.append(initStaticMethodHandle(mhMember,
                loadClass(method.getLookupClass()), method.getName(), method.getDeclareType(), method.isStatic()));
        mhMember.setInvokeExact(!method.isInvisible());
        clinit.append(mhMember.store(
                initStaticMethodHandle(loadClass(method.getLookupClass()),
                        method.getName(), method.getDeclareType(), method.isStatic())
        ));
        this.inlineAction = (varInst, args) ->
             Actions.newLocalVar(method.isStatic()
                    ? mhMember.invokeStatic(args)
                    : mhMember.invokeInstance(varInst, args));
    }


    @Override
    public ChainAction<VarInst> invoke(ChainAction<VarInst> varInstChain, Action... args) {
        return varInstChain.mapVar(varInst -> {
            // 直接内联调用 methodHandle
            return this.inlineAction.apply(varInst, args);
        });
    }
}
