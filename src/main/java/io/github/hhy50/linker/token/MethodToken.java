package io.github.hhy50.linker.token;

import io.github.hhy50.linker.define.ParseContext;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.List;

/**
 * The type Method token.
 */
public class MethodToken implements Token, ArgType {

    /**
     * The Method name.
     */
    public String methodName;
    private ArgsToken args;
    private IndexToken index;

    private boolean nullable = true;

    /**
     * Instantiates a new Method token.
     *
     * @param methodName the method name
     * @param args       the args
     */
    public MethodToken(String methodName, ArgsToken args) {
        this.methodName = methodName;
        this.args = args;
    }

    @SuppressWarnings("unchecked")
    public List<ArgType> getArgsType() {
        return (List) args.args;
    }

    @Override
    public String toString() {
        return methodName+args+(index == null ? "" : index);
    }

    @Override
    public void setIndex(IndexToken index) {
        this.index = index;
    }

    @Override
    public Type getType(ParseContext context, Method methodDefine) {
        return Type.getType(Object.class);
    }

    public ArgsToken getArgsToken() {
        return args;
    }

    @Override
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isNullable() {
        return nullable;
    }
}
