package io.github.hhy.linker.asm;

import org.objectweb.asm.MethodVisitor;

import java.util.function.Consumer;

public class MethodBuilder {

    private AsmClassBuilder classBuilder;

    private MethodVisitor methodVisitor;

    public MethodBuilder(AsmClassBuilder classBuilder, MethodVisitor methodVisitor) {
        this.classBuilder = classBuilder;
        this.methodVisitor = methodVisitor;
    }

    public AsmClassBuilder accept(Consumer<MethodVisitor> consumer) {
        consumer.accept(this.methodVisitor);
        this.methodVisitor.visitMaxs(0, 0); // auto
        return this.classBuilder;
    }

    public AsmClassBuilder getClassBuilder() {
        return classBuilder;
    }

    public MethodBuilder setClassBuilder(AsmClassBuilder classBuilder) {
        this.classBuilder = classBuilder;
        return this;
    }

    public MethodVisitor getMethodVisitor() {
        return methodVisitor;
    }

    public MethodBuilder setMethodVisitor(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
        return this;
    }
}
