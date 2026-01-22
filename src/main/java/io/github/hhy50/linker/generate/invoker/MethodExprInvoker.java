package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.asm.MethodBuilder;
import io.github.hhy50.linker.define.method.MethodExprRef;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.SmartMethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.getter.TargetFieldGetter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.List;
import java.util.function.BiFunction;

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
        this.methodName = mr.getFullName();
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        TargetFieldGetter target = classImplBuilder.getTargetGetter();
        MethodBuilder builder = classImplBuilder
                .defineMethod(Opcodes.ACC_PUBLIC, methodName, methodType, null);
        BiFunction<ChainAction<VarInst>, ChainAction<VarInst[]>, Action> invoker = (varInstChain, argsChainAction) -> {
            for (MethodRef methodRef : stepMethods) {
                MethodHandle mh = classImplBuilder.defineInvoker(methodRef);
                varInstChain = mh.invoke(ChainAction.join(varInstChain, argsChainAction));
            }
            return varInstChain.andThen(Actions.areturn(methodType.getReturnType()));
        };
        builder.intercept(invoker.apply(target.invoke(ChainAction.empty()), ChainAction.of(MethodBody::getArgs)));
    }


    public ChainAction<VarInst> invoke(ChainAction<VarInst[]> argsAction) {
        MethodInvokeAction action = new SmartMethodInvokeAction(new SmartMethodDescriptor(methodName, methodType))
                .setInstance(LoadAction.LOAD0)
                .setArgs(argsAction);
        return ChainAction.of(() -> {
                    if (methodType.getReturnType() != Type.VOID_TYPE) {
                        return Actions.newLocalVar(action);
                    }
                    return VarInst.wrap(action, methodType.getReturnType());
                });
    }

    public Type getMethodType() {
        return methodType;
    }
}
