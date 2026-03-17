package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.tools.Pair;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface ParametersLoader {

    List<Pair<Integer, Type>> analyse(Type methodType);

    ChainAction<VarInst[]> load(InvokeClassImplBuilder classImplBuilder,
                                ChainAction<VarInst[]> argsChainAction);

    static ParametersLoader all() {
        return AllParametersLoader.INSTANCE;
    }

    static ParametersLoader of(List<ParameterLoader> parameterLoaders) {
        if (parameterLoaders == null) {
            return all();
        }
        return new TokenParametersLoader(parameterLoaders);
    }
}

final class AllParametersLoader implements ParametersLoader {

    static final ParametersLoader INSTANCE = new AllParametersLoader();

    private AllParametersLoader() {
    }

    @Override
    public List<Pair<Integer, Type>> analyse(Type methodType) {
        Type[] argumentTypes = methodType.getArgumentTypes();
        return IntStream.range(0, argumentTypes.length)
                .mapToObj(i -> Pair.of(i, argumentTypes[i]))
                .collect(Collectors.toList());
    }

    @Override
    public ChainAction<VarInst[]> load(InvokeClassImplBuilder classImplBuilder,
                                       ChainAction<VarInst[]> argsChainAction) {
        return argsChainAction;
    }
}

final class TokenParametersLoader implements ParametersLoader {

    private final List<ParameterLoader> parameterLoaders;

    TokenParametersLoader(List<ParameterLoader> parameterLoaders) {
        this.parameterLoaders = Collections.unmodifiableList(new ArrayList<>(parameterLoaders));
    }

    @Override
    public List<Pair<Integer, Type>> analyse(Type methodType) {
        Type[] argumentTypes = methodType.getArgumentTypes();
        List<Pair<Integer, Type>> result = new ArrayList<>();
        for (int i = 0; i < argumentTypes.length; i++) {
            result.addAll(this.parameterLoaders.get(i).analyse(argumentTypes[i]));
        }
        return result;
    }

    @Override
    public ChainAction<VarInst[]> load(InvokeClassImplBuilder classImplBuilder,
                                       ChainAction<VarInst[]> argsChainAction) {
        define(classImplBuilder);
        return ChainAction.of(body -> {
            VarInst[] availableArgs = argsChainAction.doChain(body);
            VarInst[] args = new VarInst[this.parameterLoaders.size()];
            for (int i = 0; i < this.parameterLoaders.size(); i++) {
                args[i] = this.parameterLoaders.get(i).load(body, availableArgs);
            }
            return args;
        });
    }

    private void define(InvokeClassImplBuilder classImplBuilder) {
        for (ParameterLoader parameterLoader : this.parameterLoaders) {
            parameterLoader.define(classImplBuilder);
        }
    }
}
