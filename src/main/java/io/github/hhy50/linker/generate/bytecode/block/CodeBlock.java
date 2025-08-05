package io.github.hhy50.linker.generate.bytecode.block;

import io.github.hhy50.linker.generate.bytecode.action.Action;
import org.objectweb.asm.MethodVisitor;

/**
 * The interface Code block.
 */
public interface CodeBlock {

    /**
     * Append.
     *
     * @param action the action
     */
    void append(Action action);

    /**
     * Gets writer.
     *
     * @return the writer
     */
    MethodVisitor getWriter();
}
