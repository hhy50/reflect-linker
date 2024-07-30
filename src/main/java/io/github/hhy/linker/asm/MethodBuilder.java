package io.github.hhy.linker.asm;

import lombok.Getter;
import org.objectweb.asm.MethodVisitor;

import java.util.function.Consumer;

public class MethodBuilder {

    private AsmClassBuilder classBuilder;

    @Getter
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
}
