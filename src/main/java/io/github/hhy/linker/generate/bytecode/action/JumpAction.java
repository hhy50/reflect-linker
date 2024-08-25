package io.github.hhy.linker.generate.bytecode.action;

import io.github.hhy.linker.generate.MethodBody;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

public class JumpAction implements Action {
    private final Label label;

    public JumpAction(Label label) {
        this.label = label;
    }

    @Override
    public void apply(MethodBody body) {
        body.append(mv -> mv.visitJumpInsn(Opcodes.GOTO, label));
    }
}
