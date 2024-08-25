package io.github.hhy.linker.generate.bytecode.action;

import io.github.hhy.linker.generate.MethodBody;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;


/**
 * 条件组合语句，任一条件满足
 */
public class AnyConditionJumpAction implements Action {
    private List<Condition> conditions;
    private Action ifBlock;
    private Action elseBlock;

    public AnyConditionJumpAction(List<Condition> conditions, Action ifBlock, Action elseBlock) {
        this.conditions = conditions;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public void apply(MethodBody body) {
        final Label ifLabel = new Label();
        final Label elseLabel = new Label();
        final Label endLabel = new Label();

        MethodVisitor mv = body.getWriter();
        for (Condition condition : conditions) {
            condition.jump(body, ifLabel);
        }
        mv.visitJumpInsn(Opcodes.GOTO, elseLabel);

        mv.visitLabel(ifLabel);
        ifBlock.apply(body);
        mv.visitJumpInsn(Opcodes.GOTO, endLabel);

        mv.visitLabel(elseLabel);
        if (elseBlock != null) elseBlock.apply(body);

        mv.visitLabel(endLabel);
    }
}
