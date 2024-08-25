package io.github.hhy.linker.generate.bytecode.action;


import io.github.hhy.linker.generate.MethodBody;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public interface Condition {

    void jump(MethodBody body, Label jumpLabel);

    public static Condition isNull(Action obj) {
        return (body, jumpLabel) -> {
            MethodVisitor mv = body.getWriter();
            obj.apply(body);
            mv.visitJumpInsn(Opcodes.IFNULL, jumpLabel);
        };
    }

    public static Condition eq(Action left, Action right) {
        return (body, jumpLabel) -> {
            left.apply(body);
            right.apply(body);

            MethodVisitor mv = body.getWriter();
            mv.visitJumpInsn(Opcodes.IF_ICMPEQ, jumpLabel);
        };
    }

    public static Condition notEq(Action left, Action right) {
        return (body, jumpLabel) -> {
            left.apply(body);
            right.apply(body);

            MethodVisitor mv = body.getWriter();
            mv.visitJumpInsn(Opcodes.IF_ACMPNE, jumpLabel);
        };
    }
}
