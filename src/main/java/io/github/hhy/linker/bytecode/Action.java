package io.github.hhy.linker.bytecode;

import org.objectweb.asm.tree.InsnList;

public interface Action {

    /**
     * 执行下一个操作
     * @param insn
     */
    void doAction(InsnList insn);
}
