package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.AbsMethodDefine;
import io.github.hhy50.linker.define.method.MethodExprRef;
import io.github.hhy50.linker.generate.AbstractDecorator;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.bytecode.action.*;
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
    private final MethodExprRef methodRef;

    private final AbsMethodDefine absMethodDefine;
    private final boolean isConstructor;

    /**
     * Instantiates a new Invoker decorator.
     *
     * @param invoker         the invoker
     * @param absMethodDefine the method define
     */
    public InvokerDecorator(MethodExprInvoker invoker, AbsMethodDefine absMethodDefine) {
        super(absMethodDefine);
        Objects.requireNonNull(invoker);
        this.invoker = invoker;
        this.methodRef = absMethodDefine.methodRef;
        this.absMethodDefine = absMethodDefine;
        this.isConstructor = absMethodDefine.hasConstructor();
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        this.invoker.define(classImplBuilder);
    }

    @Override
    public ChainAction<VarInst> invoke(ChainAction<VarInst> instChainAction, ChainAction<VarInst[]> originArgs) {
//        MethodRef methodRef = absMethodDefine.methodRef;
        Type mType = methodRef.getMethodType();
        Type[] argsType = mType.getArgumentTypes();
        Class<?> rClassType = absMethodDefine.method.getReturnType();

        return (ChainAction) invoker.invoke(null, originArgs.map(a -> typecastArgs(a, argsType)))
                .mapBody((body, varInst) -> {
                    Type retType = Type.getType(rClassType);
                    if (isConstructor) {
                        return Actions.newLocalVar(new TypeCastAction(new CreateLinkerAction(retType, varInst), retType));
                    } else if (varInst != null && retType != Type.VOID_TYPE) {
                        return typecastResult(body, varInst);
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
