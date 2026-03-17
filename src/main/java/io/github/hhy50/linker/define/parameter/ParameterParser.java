package io.github.hhy50.linker.define.parameter;

import io.github.hhy50.linker.define.ParseContext;
import io.github.hhy50.linker.define.md.AbsMethodMetadata;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.exceptions.ParseException;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.action.LdcLoadAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.invoker.Getter;
import io.github.hhy50.linker.token.ArgsToken;
import io.github.hhy50.linker.token.ConstToken;
import io.github.hhy50.linker.token.PlaceholderToken;
import io.github.hhy50.linker.token.Token;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import static io.github.hhy50.linker.util.ParseUtil.getRawType;

public class ParameterParser implements ParameterLoader {

    private final ParseContext parseContext;
    private final AbsMethodMetadata metadata;
    private final ArgsToken argsToken;
    private final List<String> parameters;

    public ParameterParser(ParseContext parseContext, AbsMethodMetadata metadata, ArgsToken argsToken) throws ClassNotFoundException {
        this.parseContext = parseContext;
        this.metadata = metadata;
        this.argsToken = argsToken;
        this.parameters = new ArrayList<>();

        parse();
    }

    public String[] getParametersTypes() {
        return this.parameters.stream().toArray(String[]::new);
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
                this.parameters.add(getRawType(parameters[p]));
            } else if (item.kind() == Token.Kind.IntConst) {
                this.parameters.add("int");
            } else if (item.kind() == Token.Kind.StrConst) {
                this.parameters.add("java.lang.String");
            } else if (item.kind() == Token.Kind.Tokens
                    || item.kind() == Token.Kind.Field
                    || item.kind() == Token.Kind.Method) {

            } else {
                throw new ParseException("Invalid argument type");
            }
        }
    }

    public ChainAction<VarInst[]> loadStepArgs(MethodHandle mh, ChainAction<VarInst[]> argsChainAction) {
        if (mh instanceof Getter) {
            return ChainAction.empty();
        }

        if (argsToken == null || argsToken.isPlaceholderAll()) {
            return ChainAction.of(MethodBody::getArgs);
        }

        return argsChainAction.map(varInsts -> {
            List<VarInst> args = new ArrayList<>();
            for (Token token : argsToken) {
                switch (token.kind()) {
                    // TODO
                    case Field:
                    case Method:
                        break;
                    case StrConst:
                    case IntConst:
                        args.add(LdcLoadAction.of(((ConstToken) token).getValue()));
                        break;
                    case Placeholder:
                        args.add(varInsts[((PlaceholderToken) token).index]);
                        break;
                }
            }
            return args.toArray(new VarInst[0]);
        });
    }

    public ArgsToken getTokens() {
        return argsToken;
    }
}