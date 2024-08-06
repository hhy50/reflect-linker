package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.define.TargetField;

public class SetterMethodHandleHolder extends MethodHandleInvoker {
    private TargetField target;
    private MethodHandleInvoker prev;

    public SetterMethodHandleHolder(MethodHandleInvoker prev, Lookup lookup, String varName, boolean isStatic) {
        this.prev = prev;
    }

    public SetterMethodHandleHolder(MethodHandleInvoker prev, TargetField target) {
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
