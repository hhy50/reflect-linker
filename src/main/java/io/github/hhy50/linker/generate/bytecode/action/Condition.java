package io.github.hhy50.linker.generate.bytecode.action;


import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.Iterator;

import static io.github.hhy50.linker.generate.bytecode.action.Actions.withVisitor;


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
     */
    void jump(MethodBody body, Label jumpLabel, Label elseLabel);

    /**
     * Is null condition.
     *
     * @param obj the obj
     * @return the condition
     */
    public static Condition isNull(Action obj) {
        return (body, ifLabel, elseLabel) ->
            body.append(withVisitor(obj, mv -> {
                mv.visitJumpInsn(Opcodes.IFNULL, ifLabel);
                mv.visitJumpInsn(Opcodes.GOTO, elseLabel);
            }));
    }

    /**
     * Not null condition.
     *
     * @param obj the obj
     * @return the condition
     */
    public static Condition notNull(Action obj) {
        return (body, ifLabel, elseLabel) ->
                body.append(withVisitor(obj, mv -> {
                    mv.visitJumpInsn(Opcodes.IFNONNULL, ifLabel);
                    mv.visitJumpInsn(Opcodes.GOTO, elseLabel);
                }));
    }

    /**
     * If true condition.
     *
     * @param obj the obj
     * @return the condition
     */
    static Condition ifTrue(Action obj) {
        return (body, ifLabel, elseLabel) ->
                body.append(withVisitor(obj, mv -> {
                    mv.visitJumpInsn(Opcodes.IFNE, ifLabel);
                    mv.visitJumpInsn(Opcodes.GOTO, elseLabel);
                }));
    }

    /**
     * If false condition.
     *
     * @param obj the obj
     * @return the condition
     */
    public static Condition ifFalse(Action obj) {
        return (body, ifLabel, elseLabel) ->
                body.append(withVisitor(obj, mv -> {
                    mv.visitJumpInsn(Opcodes.IFEQ, ifLabel);
                    mv.visitJumpInsn(Opcodes.GOTO, elseLabel);
                }));
    }

    /**
     * Not eq condition.
     *
     * @param left  the left
     * @param right the right
     * @return the condition
     */
    public static Condition notEq(Action left, Action right) {
        return (body, ifLabel, elseLabel) ->
                body.append(withVisitor(left, right, mv -> {
                    mv.visitJumpInsn(Opcodes.IF_ACMPNE, ifLabel);
                    mv.visitJumpInsn(Opcodes.GOTO, elseLabel);
                }));
    }

    /**
     * Eq condition.
     *
     * @param left  the left
     * @param right the right
     * @return the condition
     */
    public static Condition eq(Action left, Action right) {
        return (body, ifLabel, elseLabel) ->
                body.append(withVisitor(left, right, mv -> {
                    mv.visitJumpInsn(Opcodes.IF_ACMPEQ, ifLabel);
                    mv.visitJumpInsn(Opcodes.GOTO, elseLabel);
                }));
    }

    /**
     * Instance of condition.
     *
     * @param obj        the obj
     * @param expectType the expect type
     * @return the condition
     */
    public static Condition instanceOf(Action obj, Type expectType) {
        return (body, ifLabel, elseLabel) ->
                body.append(withVisitor(obj, mv -> {
                    mv.visitTypeInsn(Opcodes.INSTANCEOF, expectType.getInternalName());
                    mv.visitJumpInsn(Opcodes.IFNE, ifLabel);
                    mv.visitJumpInsn(Opcodes.GOTO, elseLabel);
                }));
    }

    /**
     * Any condition.
     *
     * @param conditions the conditions
     * @return the condition
     */
    public static Condition any(Condition... conditions) {
        return (body, ifLabel, elseLabel) -> {
            Iterator<Condition> iterator = Arrays.stream(conditions).iterator();
            MethodVisitor mv = body.getWriter();

            while (iterator.hasNext()) {
                Condition condition = iterator.next();
                Label nextElse = iterator.hasNext() ? new Label() : elseLabel;
                condition.jump(body, ifLabel, nextElse);
                if (iterator.hasNext()) {
                    mv.visitLabel(nextElse);
                }
            }
        };
    }

    /**
     * Must condition.
     *
     * @param conditions the conditions
     * @return the condition
     */
    public static Condition must(Condition... conditions) {
        return (body, ifLabel, elseLabel) -> {
            MethodVisitor mv = body.getWriter();

            Iterator<Condition> iterator = Arrays.stream(conditions).iterator();
            while (iterator.hasNext()) {
                Condition condition = iterator.next();
                Label nextIf = iterator.hasNext() ? new Label() : ifLabel;
                condition.jump(body, nextIf, elseLabel);
                if (iterator.hasNext()) {
                    mv.visitLabel(nextIf);
                }
            }
        };
    }
}
