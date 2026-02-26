package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.asm.MethodBuilder;
import io.github.hhy50.linker.define.method.MethodExprRef;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.SmartMethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.utils.Methods;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.getter.TargetFieldGetter;
import io.github.hhy50.linker.runtime.RuntimeUtil;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.List;
import java.util.function.BiFunction;

/**
 * The type Method expr invoker.
 */
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
            ChainAction<VarInst[]> originArgs = argsChainAction;
            for (MethodRef methodRef : stepMethods) {
                MethodHandle mh = classImplBuilder.defineInvoker(methodRef);
                if (mh instanceof Getter) {
                    argsChainAction = varInstChain.map(varInst -> new VarInst[]{varInst});
                } else {
                    argsChainAction = ChainAction.join(varInstChain, originArgs);
                }
                varInstChain = mh.invoke(argsChainAction).map(varInst -> {
                    Type t = varInst.getType();
                    boolean nullable = methodRef.isNullable();
                    List<Object> indexs = methodRef.getIndexs();

                    // 数组访问
                    if (indexs != null && !indexs.isEmpty()) {
                        if (TypeUtil.getDimensions(t) >= indexs.size()) {
                            varInst = new ArrayIndexAction(varInst, indexs);
                        } else {
                            varInst = Methods.invoke(RuntimeUtil.INDEX_VALUE)
                                    .setArgs(varInst, Actions.asList(indexs.stream().map(LdcLoadAction::of)
                                            .map(BoxAction::new)
                                            .toArray(Action[]::new)));
                        }
                    }
                    if (nullable && !TypeUtil.isPrimitiveType(t)) {

                    }
                    return varInst;
                });
            }
            return varInstChain.areturn();
        };
        builder.intercept(invoker.apply(target.invoke(ChainAction.empty()), ChainAction.of(MethodBody::getArgs)));
    }


    public ChainAction<VarInst> invoke(ChainAction<VarInst[]> argsAction) {
        return ChainAction.of(() -> new SmartMethodInvokeAction(new SmartMethodDescriptor(methodName, methodType))
                .setInstance(LoadAction.LOAD0)
                .setArgs(argsAction));
    }

    /**
     * Gets method type.
     *
     * @return the method type
     */
    public Type getMethodType() {
        return methodType;
    }
}
