package io.github.hhy50.linker.generate.bytecode.block;

import io.github.hhy50.linker.generate.bytecode.action.Action;
import org.objectweb.asm.MethodVisitor;

public interface CodeBlock {

    /**
     *
     * @param action
     */
    void append(Action action);

    /**
     *
     * @return
     */
    MethodVisitor getWriter();
}
