package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * <p>ConditionJumpAction class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class ConditionJumpAction implements Action {
    private final Condition condition;
    private final Action ifBlock;
    private final Action elseBlock;

    /**
     * <p>Constructor for ConditionJumpAction.</p>
     *
     * @param condition a {@link io.github.hhy50.linker.generate.bytecode.action.Condition} object.
     * @param ifBlock a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     * @param elseBlock a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     */
    public ConditionJumpAction(Condition condition, Action ifBlock, Action elseBlock) {
        this.condition = condition;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }

    /** {@inheritDoc} */
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
