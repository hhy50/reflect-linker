package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.define.Field;

public class SetterMethodHandleHolder extends MethodHandleInvoker {
    private Field target;
    private MethodHandleInvoker prev;

    public SetterMethodHandleHolder(MethodHandleInvoker prev, Lookup lookup, String varName, boolean isStatic) {
        this.prev = prev;
    }

    public SetterMethodHandleHolder(MethodHandleInvoker prev, Field target) {
        this.prev = prev;
        this.target = target;
    }

    public static MethodHandleInvoker target(String bindTarget) {
        return new SetterMethodHandleHolder(null, Lookup.target(bindTarget), "target_mh", false);
    }

    @Override
    public void define(InvokeClassImplBuilder classImplBuilder) {

    }
}
