package io.github.hhy50.linker.generate.bytecode.block;


import io.github.hhy50.linker.generate.bytecode.action.Action;
import org.objectweb.asm.MethodVisitor;

public class BasicBlock implements CodeBlock {

    /**
     *
     */
    private CodeWriter writer;

    public BasicBlock(CodeWriter writer) {
        this.writer = writer;
    }

    @Override
    public void append(Action action) {

    }

    @Override
    public MethodVisitor getWriter() {
        return null;
    }
}
