package io.github.hhy.linker.generate.bytecode.action;

import io.github.hhy.linker.generate.MethodBody;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.IFNONNULL;

/**
 * 变量加载的操作
 */
public interface LoadAction extends Action {

    Action LOAD0 = new LoadAction() {
        @Override
        public void load(MethodBody body) {
            body.getWriter().visitVarInsn(Opcodes.ALOAD, 0);
        }
    };

    @Override
    default void apply(MethodBody body) {
        load(body);
    }

    default void ifNull(MethodBody body, Action ifBlock) {
        ifNull(body, ifBlock, null);
    }

    default void ifNull(MethodBody body, Action ifBlock, Action elseBlock) {
        MethodVisitor mv = body.getWriter();
        Label elseLabel = new Label();

        load(body);
        mv.visitJumpInsn(IFNONNULL, elseLabel);
        ifBlock.apply(body);

        mv.visitLabel(elseLabel);
        if (elseBlock != null)
            elseBlock.apply(body);
    }

    void load(MethodBody body);
}
