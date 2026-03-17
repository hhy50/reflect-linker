package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.action.LdcLoadAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.invoker.MethodExprInvoker;
import io.github.hhy50.linker.tools.Pair;
import org.objectweb.asm.Type;

import java.util.Collections;
import java.util.List;

public interface ParameterLoader {

    String getType();

    default List<Pair<Integer, Type>> analyse(Type parameterType) {
        return Collections.emptyList();
    }

    default void define(InvokeClassImplBuilder classImplBuilder) {
    }

    VarInst load(MethodBody body, VarInst[] availableArgs);

    static ParameterLoader placeholder(int index, String type) {
        return new PlaceholderParameterLoader(index, type);
    }

    static ParameterLoader constant(String type, Object value) {
        return new ConstParameterLoader(type, value);
    }

    static ParameterLoader expr(String type, MethodExprInvoker exprInvoker) {
        return new ExprParameterLoader(type, exprInvoker);
    }
}

final class PlaceholderParameterLoader implements ParameterLoader {

    private final int index;
    private final String type;

    PlaceholderParameterLoader(int index, String type) {
        this.index = index;
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public List<Pair<Integer, Type>> analyse(Type parameterType) {
        return Collections.singletonList(Pair.of(this.index, parameterType));
    }

    @Override
    public VarInst load(MethodBody body, VarInst[] availableArgs) {
        return availableArgs[this.index];
    }
}

final class ConstParameterLoader implements ParameterLoader {

    private final String type;
    private final Object value;

    ConstParameterLoader(String type, Object value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public VarInst load(MethodBody body, VarInst[] availableArgs) {
        return LdcLoadAction.of(this.value);
    }
}

final class ExprParameterLoader implements ParameterLoader {

    private final String type;
    private final MethodExprInvoker exprInvoker;

    ExprParameterLoader(String type, MethodExprInvoker exprInvoker) {
        this.type = type;
        this.exprInvoker = exprInvoker;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void define(InvokeClassImplBuilder classImplBuilder) {
        this.exprInvoker.define(classImplBuilder);
    }

    @Override
    public VarInst load(MethodBody body, VarInst[] availableArgs) {
        return this.exprInvoker.invoke(ChainAction.of(() -> availableArgs)).doChain(body);
    }
}
