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
        Label endLabel = this.elseBlock == null ? elseLabel : new Label();

        condition.jump(body, ifLabel, elseLabel);

        // if {}
        if (ifBlock != null) {
            mv.visitLabel(ifLabel);
            ifBlock.apply(body);
        }

        // else {}
        if (elseBlock != null) {
            mv.visitJumpInsn(Opcodes.GOTO, endLabel);
            mv.visitLabel(elseLabel);
            elseBlock.apply(body);
        }

        mv.visitLabel(endLabel);
    }
}
