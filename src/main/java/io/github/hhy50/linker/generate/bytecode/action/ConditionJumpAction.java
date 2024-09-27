package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * The type Condition jump action.
 */
public class ConditionJumpAction implements Action {
    private final Condition condition;
    private final Action ifBlock;
    private final Action elseBlock;

    /**
     * Instantiates a new Condition jump action.
     *
     * @param condition the condition
     * @param ifBlock   the if block
     * @param elseBlock the else block
     */
    public ConditionJumpAction(Condition condition, Action ifBlock, Action elseBlock) {
        this.condition = condition;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public void apply(MethodBody body) {
        MethodVisitor mv = body.getWriter();

        Label ifLabel = new Label();
        Label elseLabel = new Label();
        Label endLabel = new Label();

        condition.jump(body, ifLabel, elseLabel, endLabel);
        mv.visitJumpInsn(Opcodes.GOTO, elseLabel);

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
