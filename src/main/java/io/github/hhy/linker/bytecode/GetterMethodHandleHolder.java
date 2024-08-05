package io.github.hhy.linker.bytecode;

public class GetterMethodHandleHolder extends MethodHandleInvoker {
    private boolean isStatic;
    private String varName;
    private Lookup lookup;
    private MethodHandleInvoker prev;

    public GetterMethodHandleHolder(MethodHandleInvoker prev, Lookup lookup, String varName, boolean isStatic) {
        this.prev = prev;
        this.lookup = lookup;
        this.varName = varName;
        this.isStatic = isStatic;
    }

    public static MethodHandleInvoker target(String bindTarget) {
        return new GetterMethodHandleHolder(null, Lookup.target(bindTarget), "target_mh", false);
    }

    @Override
    public void define(InvokeClassImplBuilder classImplBuilder) {

    }
}
