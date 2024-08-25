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

    /**
     * @ifne not equals 当栈顶in类型数值不等于0时跳转
     * @ifeq equals 当栈顶int类型数值等于0时跳转
     * @param action
     * @return
     */
    static Condition wrap(Action action) {
        return (body, ifLabel) -> {
            action.apply(body);
            body.getWriter().visitJumpInsn(Opcodes.IFNE, ifLabel);
        };
    }
}
