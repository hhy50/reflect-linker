package io.github.hhy.linker.bytecode;

import org.objectweb.asm.tree.InsnList;

public abstract class AbstractAction implements Action {

    protected Action next;

    @Override
    public void doAction(InsnList insn) {
        if (next != null) {
            next.doAction(insn);
        }
    }
}
