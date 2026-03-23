package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.utils.Methods;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.function.BiFunction;

import static io.github.hhy50.linker.generate.bytecode.action.ChainAction.mapOwnerAndArgs;

/**
 * The type Early method invoker.
 */
public class DirectMethodInvoker extends Invoker<EarlyMethodRef> {
    private final Method reflect;
    protected BiFunction<VarInst, VarInst[], VarInst> inlineAction;

    /**
     * Instantiates a new Early method invoker.
     *
     * @param mr the method ref
     */
    public DirectMethodInvoker(EarlyMethodRef mr) {
        super(mr.getName(), mr.getLookupType(), mr.getSuperClass());
        this.reflect = mr.getReflect();
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        this.inlineAction = (varInst, args) -> Methods.invoke(reflect)
                .setInstance(varInst.cast(Type.getType(reflect.getDeclaringClass())))
                .setArgs(args);
    }

    @Override
    public ChainAction<VarInst> invoke(ChainAction<VarInst[]> argsAction) {
        return mapOwnerAndArgs(argsAction, this.inlineAction);
    }
}
