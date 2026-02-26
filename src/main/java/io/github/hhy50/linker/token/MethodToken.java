package io.github.hhy50.linker.token;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Method token.
 */
public class MethodToken implements Token {

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

    @Override
    public Kind kind() {
        return Kind.Method;
    }

    @Override
    public String toString() {
        return methodName+args+(index == null ? "" : index);
    }

    @Override
    public void setIndex(List<ConstToken> index) {
        this.index = new IndexToken(index);
    }

    /**
     * Gets indexs.
     *
     * @return the indexs
     */
    public List<Object> getIndexs() {
        if (this.index == null) return new ArrayList<>();
        return this.index.toValues();
    }

    /**
     * Gets args token.
     *
     * @return the args token
     */
    public ArgsToken getArgsToken() {
        return args;
    }

    @Override
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    /**
     * Is nullable boolean.
     *
     * @return the boolean
     */
    public boolean isNullable() {
        return nullable;
    }
}
