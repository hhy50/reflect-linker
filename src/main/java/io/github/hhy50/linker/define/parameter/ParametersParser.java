package io.github.hhy50.linker.define.parameter;

import io.github.hhy50.linker.define.ParseContext;
import io.github.hhy50.linker.define.md.AbsMethodMetadata;
import io.github.hhy50.linker.define.method.ParametersLoader;
import io.github.hhy50.linker.exceptions.ParseException;
import io.github.hhy50.linker.generate.invoker.MethodExprInvoker;
import io.github.hhy50.linker.token.*;
import io.github.hhy50.linker.util.ParseUtil;
import org.objectweb.asm.Type;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import static io.github.hhy50.linker.util.ParseUtil.getRawType;

public class ParametersParser {
    private final ParseContext parseContext;
    private final AbsMethodMetadata metadata;
    private final ArgsToken tokens;
    private final List<ParameterLoader> parameters;
    private ParametersLoader parametersLoader;

    public ParametersParser(ParseContext parseContext, AbsMethodMetadata metadata, ArgsToken tokens) throws ClassNotFoundException {
        this.parseContext = parseContext;
        this.metadata = metadata;
        this.tokens = tokens;
        this.parameters = new ArrayList<>();

        parse();
    }

    public String[] getParametersTypes() {
        return this.parameters.stream().map(ParameterLoader::getType).toArray(String[]::new);
    }

    public ParametersLoader getParametersLoader() {
        return this.parametersLoader;
    }

    void parse() throws ClassNotFoundException {
        Parameter[] parameters = metadata.getParameters();
        if (metadata.isConstructor() || tokens.isPlaceholderAll()) {
            for (int i = 0; i < parameters.length; i++) {
                this.parameters.add(ParameterLoader.placeholder(i, ParseUtil.getRawType(parameters[i])));
            }
            this.parametersLoader = ParametersLoader.all();
            return;
        }

        for (int i = 0; i < tokens.size(); i++) {
            Token item = tokens.get(i);
            if (item.kind() == Token.Kind.Placeholder) {
                int p = ((PlaceholderToken) item).index;
                if (p < 0 || p >= parameters.length) {
                    throw new ParseException("Parameter index out of bounds for parameter: $" + p);
                }
                this.parameters.add(ParameterLoader.placeholder(p, getRawType(parameters[p])));
            } else if (item.kind() == Token.Kind.IntConst) {
                this.parameters.add(ParameterLoader.constant("int", ((ConstToken) item).getValue()));
            } else if (item.kind() == Token.Kind.StrConst) {
                this.parameters.add(ParameterLoader.constant("java.lang.String", ((ConstToken) item).getValue()));
            } else if (item.kind() == Token.Kind.Tokens
                    || item.kind() == Token.Kind.Field
                    || item.kind() == Token.Kind.Method) {
                MethodExprInvoker argExprInvoker = parseArgInvoker(item);
                Type argType = argExprInvoker.getMethodType().getReturnType();
                if (argType == Type.VOID_TYPE) {
                    throw new ParseException("Invalid argument type");
                }
                this.parameters.add(ParameterLoader.expr(argType.getClassName(), argExprInvoker));
            } else {
                throw new ParseException("Invalid argument type");
            }
        }
        this.parametersLoader = ParametersLoader.of(this.parameters);
    }

    public MethodExprInvoker parseArgInvoker(Token token) throws ClassNotFoundException {
        if (token instanceof Tokens) {
            return this.parseContext.parseMethodExpr(metadata, (Tokens) token, false).defineInvoker();
        }
        Tokens tokens = new Tokens();
        tokens.add(token);
        return this.parseContext.parseMethodExpr(metadata, tokens, false).defineInvoker();
    }
}