package io.github.hhy50.linker.generate.bytecode.action;


import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


/**
 * The interface Condition.
 */
public interface Condition {

    /**
     * Jump.
     *
     * @param body      the body
     * @param jumpLabel the jump label
     * @param elseLabel the else label
     * @param endLabel  the end label
     */
    void jump(MethodBody body, Label jumpLabel, Label elseLabel, Label endLabel);

    /**
     * Is null condition.
     *
     * @param obj the obj
     * @return the condition
     */
    public static Condition isNull(Action obj) {
        return (body, ifLabel, elseLabel, endLabel) -> {
            MethodVisitor mv = body.getWriter();
            obj.apply(body);
            mv.visitJumpInsn(Opcodes.IFNULL, ifLabel);
        };
    }

    /**
     * Not null condition.
     *
     * @param obj the obj
     * @return the condition
     */
    public static Condition notNull(Action obj) {
        return (body, ifLabel, elseLabel, endLabel) -> {
            MethodVisitor mv = body.getWriter();
            obj.apply(body);
            mv.visitJumpInsn(Opcodes.IFNONNULL, ifLabel);
        };
    }

    /**
     * If true condition.
     *
     * @param action the action
     * @return the condition
     */
    static Condition ifTrue(Action action) {
        return (body, ifLabel, elseLabel, endLabel) -> {
            action.apply(body);
            body.getWriter().visitJumpInsn(Opcodes.IFNE, ifLabel);
        };
    }

    /**
     * If false condition.
     *
     * @param obj the obj
     * @return the condition
     */
    public static Condition ifFalse(Action obj) {
        return (body, ifLabel, elseLabel, endLabel) -> {
            MethodVisitor mv = body.getWriter();
            obj.apply(body);
            mv.visitJumpInsn(Opcodes.IFEQ, ifLabel);
        };
    }

    /**
     * Not eq condition.
     *
     * @param left  the left
     * @param right the right
     * @return the condition
     */
    public static Condition notEq(Action left, Action right) {
        return (body, ifLabel, elseLabel, endLabel) -> {
            left.apply(body);
            right.apply(body);

            MethodVisitor mv = body.getWriter();
            mv.visitJumpInsn(Opcodes.IF_ACMPNE, ifLabel);
        };
    }

    /**
     * Eq condition.
     *
     * @param left  the left
     * @param right the right
     * @return the condition
     */
    public static Condition eq(Action left, Action right) {
        return (body, ifLabel, elseLabel, endLabel) -> {
            left.apply(body);
            right.apply(body);

            MethodVisitor mv = body.getWriter();
            mv.visitJumpInsn(Opcodes.IF_ACMPEQ, ifLabel);
        };
    }

    /**
     * Instance of condition.
     *
     * @param obj        the obj
     * @param expectType the expect type
     * @return the condition
     */
    public static Condition instanceOf(Action obj, Type expectType) {
        return (body, ifLabel, elseLabel, endLabel) -> {
            obj.apply(body);

            MethodVisitor mv = body.getWriter();
            mv.visitTypeInsn(Opcodes.INSTANCEOF, expectType.getInternalName());
            mv.visitJumpInsn(Opcodes.IFNE, ifLabel);
        };
    }

    /**
     * Any condition.
     *
     * @param conditions the conditions
     * @return the condition
     */
    public static Condition any(Condition... conditions) {
        return (body, ifLabel, elseLabel, endLabel) -> {
            for (Condition condition : conditions) {
                condition.jump(body, ifLabel, elseLabel, endLabel);
            }
            body.getWriter().visitJumpInsn(Opcodes.GOTO, elseLabel);
        };
    }

    /**
     * Must condition.
     *
     * @param conditions the conditions
     * @return the condition
     */
    public static Condition must(Condition... conditions) {
        return (body, ifLabel, elseLabel, endLabel) -> {
            MethodVisitor mv = body.getWriter();
            for (int i = 0; i < conditions.length; i++) {
                Label nextIf = i == conditions.length - 1 ? ifLabel : new Label();
                conditions[i].jump(body, nextIf, elseLabel, endLabel);
                mv.visitJumpInsn(Opcodes.GOTO, elseLabel);
                mv.visitLabel(nextIf);
            }
        };
    }
}
