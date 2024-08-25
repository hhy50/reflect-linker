package io.github.hhy.linker.generate.bytecode.action;

import io.github.hhy.linker.generate.MethodBody;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ConditionJumpAction implements Action {
    private final Action condition;
    private final Action ifBlock;
    private final Action elseBlock;

    public ConditionJumpAction(Action condition, Action ifBlock, Action elseBlock) {
        this.condition = condition;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public void apply(MethodBody body) {
        condition.apply(body);

        MethodVisitor mv = body.getWriter();

        Label ifLabel = new Label();
        Label elseLabel = new Label();
        Label endLabel = new Label();
        mv.visitJumpInsn(Opcodes.IFEQ, elseLabel);

        // if {}
        mv.visitLabel(ifLabel);
        if (ifBlock != null)
            ifBlock.apply(body);
        mv.visitJumpInsn(Opcodes.GOTO, endLabel);

        // else {}
        mv.visitLabel(elseLabel);
        if (elseBlock != null)
            elseBlock.apply(body);

        mv.visitLabel(endLabel);
    }
}
