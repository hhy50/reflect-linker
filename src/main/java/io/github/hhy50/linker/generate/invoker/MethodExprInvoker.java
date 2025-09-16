package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.method.MethodExprRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

import java.util.List;

public class MethodExprInvoker extends Invoker<MethodExprRef> {
    List<MethodHandle> invokers;
    /**
     * Instantiates a new Invoker.
     *
     * @param method the method
     * @param mType  the m type
     */
    public MethodExprInvoker(MethodExprRef method, Type mType) {
        super(method, mType);
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {

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
            return Actions.newLocalVar(new MethodInvokeAction(descriptor)
                    .setInstance(varInst)
                    .setArgs(argsChainAction));
        });
    }

}
