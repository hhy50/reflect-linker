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


    /**
     * Instantiates a new Method token.
     */
    public MethodToken(String methodName, List<Token> args) {
        this.methodName = methodName;
        this.args = args;
    }

    @Override
    public String value() {
        return methodName;
    }
}
