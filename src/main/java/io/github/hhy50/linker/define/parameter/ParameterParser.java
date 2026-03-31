package io.github.hhy50.linker.define.parameter;

import io.github.hhy50.linker.define.ParseContext;
import io.github.hhy50.linker.define.md.AbsMethodMetadata;
import io.github.hhy50.linker.exceptions.ParseException;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.action.LdcLoadAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.invoker.Getter;
import io.github.hhy50.linker.generate.invoker.MethodExprInvoker;
import io.github.hhy50.linker.token.*;
import org.objectweb.asm.Type;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import static io.github.hhy50.linker.generate.MethodHandle.typeCast;
import static io.github.hhy50.linker.util.ParseUtil.getRawType;

public class ParameterParser implements ParameterLoader {

    private final ParseContext parseContext;
    private final AbsMethodMetadata metadata;
    private final ArgsToken argsToken;
    private final List<String> parameters;
    private final List<ArgumentLoader> argumentLoaders;

    public ParameterParser(ParseContext parseContext, AbsMethodMetadata metadata, ArgsToken argsToken) throws ClassNotFoundException {
        this.parseContext = parseContext;
        this.metadata = metadata;
        this.argsToken = argsToken;
        this.parameters = new ArrayList<>();
        this.argumentLoaders = new ArrayList<>();

        parse();
    }

    public String[] getParameterTypes() {
        return this.parameters.stream().toArray(String[]::new);
    }

    public ParameterLoader getParameterLoader() {
        return this;
    }

    void parse() throws ClassNotFoundException {
        Parameter[] parameters = metadata.getParameters();
        if (metadata.isConstructor() || argsToken.isPlaceholderAll()) {
            for (Parameter parameter : parameters) {
                this.parameters.add(getRawType(parameter));
            }
            return;
        }

        for (int i = 0; i < argsToken.size(); i++) {
            Token item = argsToken.get(i);
            if (item.kind() == Token.Kind.Placeholder) {
                int p = ((PlaceholderToken) item).index;
                if (p < 0 || p >= parameters.length) {
                    throw new ParseException("Parameter index out of bounds for parameter: $" + p);
                }
                String parameterType = getRawType(parameters[p]);
                this.parameters.add(parameterType);
                this.argumentLoaders.add(new PlaceholderArgumentLoader(p, parameterType));
            } else if (item.kind() == Token.Kind.IntConst) {
                this.parameters.add("int");
                this.argumentLoaders.add(new ConstantArgumentLoader((ConstToken) item, "int"));
            } else if (item.kind() == Token.Kind.StrConst) {
                this.parameters.add("java.lang.String");
                this.argumentLoaders.add(new ConstantArgumentLoader((ConstToken) item, "java.lang.String"));
            } else if (item.kind() == Token.Kind.Tokens
                    || item.kind() == Token.Kind.Field
                    || item.kind() == Token.Kind.Method) {
                ArgumentLoader argumentLoader = parseExprArgument(item);
                this.argumentLoaders.add(argumentLoader);
                this.parameters.add(argumentLoader.getParameterType());
            } else {
                throw new ParseException("Invalid argument type");
            }
        }
    }

    @Override
    public void define(InvokeClassImplBuilder classImplBuilder) {
        for (ArgumentLoader argumentLoader : argumentLoaders) {
            argumentLoader.define(classImplBuilder);
        }
    }

    public ChainAction<VarInst[]> loadStepArgs(MethodHandle mh, ChainAction<VarInst[]> methodArgs) {
        if (mh instanceof Getter) {
            return ChainAction.empty();
        }

        if (argsToken == null || argsToken.isPlaceholderAll()) {
            return ChainAction.of(MethodBody::getArgs);
        }

        ChainAction<VarInst[]> loaded = ChainAction.of(() -> new VarInst[0]);
        for (ArgumentLoader argumentLoader : argumentLoaders) {
            loaded = ChainAction.join2(loaded, argumentLoader.load(methodArgs));
        }
        return loaded;
    }

    @Override
    public ArgsToken getArgsToken() {
        return argsToken;
    }

    private ArgumentLoader parseExprArgument(Token token) throws ClassNotFoundException {
        MethodExprInvoker exprInvoker = parseContext.parseMethodExpr(metadata, asTokens(token)).defineInvoker();
        Type returnType = exprInvoker.getMethodType().getReturnType();
        if (Type.VOID_TYPE.equals(returnType)) {
            throw new ParseException("Invalid argument type");
        }
        return new ExprArgumentLoader(exprInvoker, returnType.getClassName());
    }

    private Tokens asTokens(Token token) {
        if (token.kind() == Token.Kind.Tokens) {
            return (Tokens) token;
        }
        Tokens tokens = new Tokens();
        tokens.add(token);
        return tokens;
    }

    private interface ArgumentLoader {

        String getParameterType();

        ChainAction<VarInst> load(ChainAction<VarInst[]> methodArgs);

        default void define(InvokeClassImplBuilder classImplBuilder) {
        }
    }

    private static class PlaceholderArgumentLoader implements ArgumentLoader {
        private final int index;
        private final String parameterType;

        private PlaceholderArgumentLoader(int index, String parameterType) {
            this.index = index;
            this.parameterType = parameterType;
        }

        @Override
        public String getParameterType() {
            return parameterType;
        }

        @Override
        public ChainAction<VarInst> load(ChainAction<VarInst[]> methodArgs) {
            return methodArgs.map(vars -> vars[index]);
        }
    }

    private static class ConstantArgumentLoader implements ArgumentLoader {
        private final ConstToken token;
        private final String parameterType;

        private ConstantArgumentLoader(ConstToken token, String parameterType) {
            this.token = token;
            this.parameterType = parameterType;
        }

        @Override
        public String getParameterType() {
            return parameterType;
        }

        @Override
        public ChainAction<VarInst> load(ChainAction<VarInst[]> methodArgs) {
            return ChainAction.of(() -> LdcLoadAction.of(token.getValue()));
        }
    }

    private static class ExprArgumentLoader implements ArgumentLoader {
        private final MethodExprInvoker exprInvoker;
        private final String parameterType;
        private final Type methodType;


        private ExprArgumentLoader(MethodExprInvoker exprInvoker, String parameterType) {
            this.exprInvoker = exprInvoker;
            this.parameterType = parameterType;
            this.methodType = exprInvoker.getMethodType();
        }

        @Override
        public String getParameterType() {
            return parameterType;
        }

        @Override
        public ChainAction<VarInst> load(ChainAction<VarInst[]> methodArgs) {
            return exprInvoker.invoke(methodArgs.map(varInsts -> {
                Type[] argumentTypes = methodType.getArgumentTypes();
                for (int i = 0; i < argumentTypes.length; i++) {
                    varInsts[i] = typeCast(varInsts[i], argumentTypes[i]);
                }
                return varInsts;
            }));
        }

        @Override
        public void define(InvokeClassImplBuilder classImplBuilder) {
            exprInvoker.define(classImplBuilder);
        }
    }
}
