package io.github.hhy.linker.bytecode;

import org.objectweb.asm.tree.InsnList;

public interface Action {

    void action(InsnList insn);
}
