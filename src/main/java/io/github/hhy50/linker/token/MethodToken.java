package io.github.hhy50.linker.token;

/**
 * The type Method token.
 */
public class MethodToken extends Token {

    /**
     * The Method name.
     */
    public String methodName;


    /**
     * Instantiates a new Method token.
     */
    public MethodToken() {

    }


    @Override
    public String value() {
        return methodName;
    }
}
