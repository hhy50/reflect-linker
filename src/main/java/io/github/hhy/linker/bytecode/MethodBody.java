package io.github.hhy.linker.bytecode;

import org.objectweb.asm.MethodVisitor;

import java.util.function.Consumer;

public class MethodBody {

    public final MethodVisitor writer;

    public int lvbIndex;

    public MethodBody(MethodVisitor writer, boolean isStatic) {
        this.writer = writer;
        this.lvbIndex = isStatic ? 0: 1;
    }

    public void append(Consumer<MethodVisitor> interceptor) {
        interceptor.accept(this.writer);
    }

    public void end() {
        this.writer.visitMaxs(0, 0);
    }
}
