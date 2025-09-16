package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.AbsMethodDefine;
import io.github.hhy50.linker.define.method.MethodRef;
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
    protected Invoker<?> realInvoker;
    private final AbsMethodDefine absMethodDefine;
    private final boolean isConstructor;

    /**
     * Instantiates a new Invoker decorator.
     *
     * @param realInvoker     the real invoker
     * @param absMethodDefine the method define
     */
    public InvokerDecorator(Invoker<?> realInvoker, AbsMethodDefine absMethodDefine) {
        super(absMethodDefine);
        Objects.requireNonNull(realInvoker);

        this.realInvoker = realInvoker;
        this.absMethodDefine = absMethodDefine;
        this.isConstructor = absMethodDefine.hasConstructor();
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        this.realInvoker.define(classImplBuilder);
    }

    @Override
    public ChainAction<VarInst> invoke(ChainAction<VarInst> instChainAction, ChainAction<VarInst[]> originArgs) {
        MethodRef methodRef = absMethodDefine.methodRef;
        Type[] argsType = methodRef.getMethodType().getArgumentTypes();
        Class<?> rClassType = absMethodDefine.method.getReturnType();

        return realInvoker.invoke(null, originArgs.map(a -> typecastArgs(a, argsType)))
                .then(varInst -> {
                    return null;
                });
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
