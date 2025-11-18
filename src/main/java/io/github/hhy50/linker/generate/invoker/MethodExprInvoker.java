package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.asm.MethodBuilder;
import io.github.hhy50.linker.define.SmartMethodDescriptor;
import io.github.hhy50.linker.define.method.MethodExprRef;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.getter.TargetFieldGetter;
import io.github.hhy50.linker.util.RandomUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MethodExprInvoker extends Invoker<MethodExprRef> {
    private final Type methodType;
    private final String methodName;
    private final List<MethodRef> stepMethods;

    /**
     * Instantiates a new Invoker.
     *
     * @param mr the method
     */
    public MethodExprInvoker(MethodExprRef mr) {
        super(mr.getName(), null);
        this.stepMethods = mr.getStepMethods();
        this.methodType = mr.getMethodType();
        this.methodName = "invoke_" + RandomUtil.getRandomString(6);

    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        TargetFieldGetter target = classImplBuilder.getTargetGetter();
        MethodBuilder builder = classImplBuilder
                .defineMethod(Opcodes.ACC_PUBLIC, methodName, methodType, null);
        BiFunction<ChainAction<VarInst>, ChainAction<VarInst[]>, Action> invoker = (varInstChain, argsChainAction) -> {
            for (MethodRef methodRef : stepMethods) {
                MethodHandle mh = methodRef.defineInvoker();
                mh.define(classImplBuilder);
                varInstChain = mh.invoke(varInstChain, argsChainAction);
            }
            return varInstChain.andThen(Actions.areturn(methodType.getReturnType()));
        };
        builder.intercept(invoker.apply(target.invoke(null, null), new ChainAction2<>(MethodBody::getArgs)));
    }

    static class ChainAction2<T> extends ChainAction<T> {
        ChainAction2(Function<MethodBody, T> func) {
            super(func);
        }

        @Override
        public void apply(MethodBody body) {
            super.apply(body);
        }
    }

    public ChainAction<VarInst> invoke(ChainAction<VarInst> varInstChain, ChainAction<VarInst[]> argsChainAction) {
//        for (MethodHandle invoker : invokers) {
//            varInstChain = invoker.invoke(varInstChain, argsChainAction);
//        }
//        return varInstChain;
        return argsChainAction.map(args ->
                new SmartMethodInvokeAction(new SmartMethodDescriptor(methodName, methodType))
                        .setInstance(LoadAction.LOAD0)
                        .setArgs(args)
        ).map(Actions::newLocalVar);
    }

}
