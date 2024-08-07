package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.asm.AsmClassBuilder;

public class InvokeClassImplBuilder extends AsmClassBuilder {
    public String bindTarget;

    public InvokeClassImplBuilder(int access, String className, String superName, String[] interfaces, String signature) {
        super(access, className, superName, interfaces, signature);
    }

    public InvokeClassImplBuilder setTarget(String bindTarget) {
        this.bindTarget = bindTarget;
        return this;
    }
}
