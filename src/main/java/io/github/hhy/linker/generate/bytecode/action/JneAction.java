package io.github.hhy.linker.generate.bytecode.action;

import io.github.hhy.linker.generate.MethodBody;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class JneAction implements Action {

    private final Action condition1;
    private final Action condition2;
    private final Action ifBlock;
    private final Action elseBlock;

    public JneAction(Action condition1, Action condition2, Action ifBlock, Action elseBlock) {
        this.condition1 = condition1;
        this.condition2 = condition2;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public void apply(MethodBody body) {
        condition1.apply(body);
        condition2.apply(body);

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
