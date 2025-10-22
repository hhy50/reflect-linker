package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.method.MethodExprRef;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;

import java.util.List;

public class MethodExprInvoker extends Invoker<MethodExprRef> {
    List<MethodHandle> invokers;

    private final List<MethodRef> stepMethods;

    /**
     * Instantiates a new Invoker.
     *
     * @param mr the method
     */
    public MethodExprInvoker(MethodExprRef mr) {
        super(mr.getName(), mr.getMhType());
        this.stepMethods = mr.getStepMethods();
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        for (MethodRef methodRef : stepMethods) {
            MethodHandle mh = methodRef.defineInvoker();
            mh.define(classImplBuilder);
            invokers.add(mh);
        }
    }

    public ChainAction<VarInst> invoke(ChainAction<VarInst> varInstChain, ChainAction<VarInst[]> argsChainAction) {
        for (MethodHandle invoker : invokers) {
            varInstChain = invoker.invoke(varInstChain, argsChainAction)
                    .then(varInst -> {
                        // 执行扩展
                        return null;
                    });
        }

        return varInstChain.then(varInst -> {
            return Actions.newLocalVar(new MethodInvokeAction(null)
                    .setInstance(varInst)
                    .setArgs(argsChainAction));
        });
    }

}
