package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.md.AbsMethodMetadata;
import io.github.hhy50.linker.generate.AbstractDecorator;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
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

    /**
     * Instantiates a new Invoker decorator.
     *
     * @param invoker  the invoker
     * @param metadata the metadata
     */
    public InvokerDecorator(MethodExprInvoker invoker, AbsMethodMetadata metadata) {
        super(metadata);
        Objects.requireNonNull(invoker);
        this.invoker = invoker;
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        this.invoker.define(classImplBuilder);
    }

    @Override
    public ChainAction<VarInst> invoke(ChainAction<VarInst[]> argsAction) {
        Type mType = invoker.getMethodType();
        Type[] argsType = mType.getArgumentTypes();

        return typecastResult(invoker.invoke(argsAction.map(a -> typecastArgs(a, argsType))))
                .then(VarInst::thenReturn);
    }
}
