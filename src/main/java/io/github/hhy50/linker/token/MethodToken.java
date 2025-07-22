package io.github.hhy50.linker.token;

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
    public String value() {
        return methodName;
    }

    @Override
    public String toString() {
        return methodName+args+(index == null ? "" : index);
    }

    public void setIndex(IndexToken index) {
        this.index = index;
    }
}
