package io.github.hhy50.linker.token;

import java.util.List;

/**
 * The type Method token.
 */
public class MethodToken implements Token {

    /**
     * The Method name.
     */
    public String methodName;
    private final List<Token> args;
    private IndexToken index;

    /**
     * Instantiates a new Method token.
     *
     * @param methodName the method name
     * @param args       the args
     */
    public MethodToken(String methodName, List<Token> args) {
        this.methodName = methodName;
        this.args = args;
    }

    @Override
    public String value() {
        return methodName;
    }

    @Override
    public String toString() {
        return methodName+("")+(index == null ? "" : index.toString());
    }

    public void setIndex(IndexToken index) {
        this.index = index;
    }
}
