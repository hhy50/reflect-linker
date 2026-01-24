package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.md.AbsMethodMetadata;
import io.github.hhy50.linker.generate.AbstractDecorator;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.action.CreateLinkerAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

import java.util.Objects;

/**
 * The type Invoker decorator.
 */
public class InvokerDecorator extends AbstractDecorator {

    /**
     * The Real invoker.
     */
    protected MethodExprInvoker invoker;
    private final AbsMethodMetadata metadata;
    private final boolean isConstructor;

    /**
     * Instantiates a new Invoker decorator.
     *
     * @param invoker         the invoker
     */
    public InvokerDecorator(MethodExprInvoker invoker, AbsMethodMetadata metadata) {
        super(metadata);
        Objects.requireNonNull(invoker);
        this.invoker = invoker;
        this.metadata = metadata;
        this.isConstructor = metadata.isConstructor();
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        this.invoker.define(classImplBuilder);
    }

    @Override
    public ChainAction<VarInst> invoke(ChainAction<VarInst[]> argsAction) {
        Type mType = invoker.getMethodType();
        Type[] argsType = mType.getArgumentTypes();
        Class<?> rClassType = metadata.getReflect().getReturnType();

        return (ChainAction) invoker.invoke(argsAction.map(a -> typecastArgs(a, argsType)))
                .mapBody((body, varInst) -> {
                    Type retType = Type.getType(rClassType);
                    if (isConstructor) {
                        return new CreateLinkerAction(retType, varInst);
                    } else if (varInst != null && retType != Type.VOID_TYPE) {
                        return typecastResult(body, Actions.newLocalVar(varInst));
                    }
                    return varInst;
                })
                .then(VarInst::thenReturn);
//        return ChainAction.of(() -> typecastArgs(originArgs, argsType))
//                .map(realArgs -> )
//                .map(varInst -> {
//                    Type retType = Type.getType(rClassType);
//                    if (isConstructor) {
//                        return new TypeCastAction(new CreateLinkerAction(retType, varInst), retType);
//                    } else if (varInst != null && retType != Type.VOID_TYPE) {
//                        return typecastResult(methodBody, varInst);
//                    }
//                })
//
//
//
//        return ChainAction.of(body -> args1)
//                .map(body -> realInvoker.invoke(null,  args))
//                .map(varInst -> {
//                    Type retType = Type.getType(rClassType);
//                    if (isConstructor) {
//                        return new TypeCastAction(new CreateLinkerAction(retType, varInst), retType);
//                    } else if (varInst != null && retType != Type.VOID_TYPE) {
//                        return typecastResult(methodBody, varInst);
//                    }
//                    return (VarInst) null;
//                })
//                .map(varInst -> {
//                    if (varInst != null) {
//                        return varInst.thenReturn();
//                    } else {
//                        return Actions.vreturn();
//                    }
//                });
    }
}
