package io.github.hhy.linker.generate.bytecode.action;

import io.github.hhy.linker.generate.MethodBody;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class JneAction implements Action {

    private final Action left;
    private final Action rift;
    private final Action ifBlock;
    private final Action elseBlock;

    public JneAction(Action left, Action rift, Action ifBlock, Action elseBlock) {
        this.left = left;
        this.rift = rift;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public void apply(MethodBody body) {
        left.apply(body);
        rift.apply(body);

        MethodVisitor mv = body.getWriter();

        Label neqLabel = new Label();
        Label eqLabel = new Label();
        Label endLabel = new Label();
        mv.visitJumpInsn(Opcodes.IF_ACMPEQ, eqLabel);

        // if {}
        mv.visitLabel(neqLabel);
        if (ifBlock != null)
            ifBlock.apply(body);
        mv.visitJumpInsn(Opcodes.GOTO, endLabel);

        // else {}
        mv.visitLabel(eqLabel);
        if (elseBlock != null)
            elseBlock.apply(body);

        mv.visitLabel(endLabel);
    }
}