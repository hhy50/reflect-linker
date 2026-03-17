package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.define.md.AbsMethodMetadata;
import io.github.hhy50.linker.define.parameter.ParameterParser;
import io.github.hhy50.linker.exceptions.ParseException;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.invoker.MethodExprInvoker;
import io.github.hhy50.linker.token.ArgsToken;
import io.github.hhy50.linker.token.PlaceholderToken;
import org.objectweb.asm.Type;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Method expr ref.
 */
public class MethodExprRef extends MethodRef {

    private final AbsMethodMetadata metadata;
    private final List<MethodRef> stepMethods;
    private final Type[] parameterTypes;
    private Type returnType = Type.VOID_TYPE;
    private Map<MethodRef, ParameterParser> parameterParsers;

    /**
     * Instantiates a new Method ref.
     *
     * @param metadata the metadata
     */
    public MethodExprRef(AbsMethodMetadata metadata) {
        super(metadata.getName());
        this.metadata = metadata;
        this.stepMethods = new ArrayList<>();
        this.parameterTypes = Arrays.stream(metadata.getParameters()).map(p -> Type.getType(p.getType())).toArray(Type[]::new);
        this.parameterParsers = new HashMap<>();
    }

    /**
     * Add step method.
     *
     * @param methodRef the method ref
     */
    public void addStepMethod(MethodRef methodRef) {
        stepMethods.add(methodRef);
        this.returnType = methodRef.getReturnType();
    }

    public void addStepMethod(MethodRef methodRef, ArgsToken argsToken) {
        stepMethods.add(methodRef);
        if (argsToken != null) {
            Type type = methodRef.getGenericType();
            this.analyse(type, argsToken);
        }
        this.returnType = methodRef.getReturnType();
    }

    public void analyse(Type methodType, ArgsToken argsToken) {
        Map<Integer, Type> indexTable = new HashMap<>();
        Type[] argumentTypes = methodType.getArgumentTypes();
        if (argsToken.isPlaceholderAll()) {
            for (int i = 0; i < argumentTypes.length; i++) {
                indexTable.put(i, argumentTypes[i]);
            }
        } else {
            Class<PlaceholderToken> __ = PlaceholderToken.class;
            indexTable = argsToken.stream().filter(item -> item instanceof PlaceholderToken).map(__::cast)
                    .collect(Collectors.toMap(PlaceholderToken::getIndex, p -> argumentTypes[p.index]));
        }

        for (Map.Entry<Integer, Type> entry : indexTable.entrySet()) {
            int index = entry.getKey();
            Type type = entry.getValue();

            if (index >= this.parameterTypes.length) {
                throw new ParseException("Parameter index out of bounds for parameter: $" + index);
            }

            if (this.parameterTypes[index] != null && !this.parameterTypes[index].equals(type)) {
                this.parameterTypes[index] = ObjectVar.TYPE;
            } else {
                this.parameterTypes[index] = type;
            }
        }
    }

    /**
     * Gets step methods.
     *
     * @return the step methods
     */
    public List<MethodRef> getStepMethods() {
        return stepMethods;
    }

    /**
     * Gets method type.
     *
     * @return the method type
     */
    public Type getMethodType() {
        return Type.getMethodType(this.returnType, this.parameterTypes);
    }

    @Override
    public Type getLookupType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MethodExprInvoker defineInvoker() {
        return new MethodExprInvoker(this);
    }

    /**
     * Gets metadata.
     *
     * @return the metadata
     */
    public AbsMethodMetadata getMetadata() {
        return this.metadata;
    }

    public ParameterParser getParameterParser(MethodRef methodRef) {
        return this.parameterParsers.get(methodRef);
    }
}
