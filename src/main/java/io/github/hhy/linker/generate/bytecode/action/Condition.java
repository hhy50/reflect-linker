package io.github.hhy.linker.generate.bytecode.action;


import io.github.hhy.linker.generate.MethodBody;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


/**
 * <p>Condition interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public interface Condition {

    /**
     * <p>jump.</p>
     *
     * @param body a {@link io.github.hhy.linker.generate.MethodBody} object.
     * @param jumpLabel a {@link org.objectweb.asm.Label} object.
     * @param elseLabel a {@link org.objectweb.asm.Label} object.
     * @param endLabel a {@link org.objectweb.asm.Label} object.
     */
    void jump(MethodBody body, Label jumpLabel, Label elseLabel, Label endLabel);

    /**
     * <p>isNull.</p>
     *
     * @param obj a {@link io.github.hhy.linker.generate.bytecode.action.Action} object.
     * @return a {@link io.github.hhy.linker.generate.bytecode.action.Condition} object.
     */
    public static Condition isNull(Action obj) {
        return (body, ifLabel, elseLabel, endLabel) -> {
            MethodVisitor mv = body.getWriter();
            obj.apply(body);
            mv.visitJumpInsn(Opcodes.IFNULL, ifLabel);
        };
    }

    /**
     * <p>notNull.</p>
     *
     * @param obj a {@link io.github.hhy.linker.generate.bytecode.action.Action} object.
     * @return a {@link io.github.hhy.linker.generate.bytecode.action.Condition} object.
     */
    public static Condition notNull(Action obj) {
        return (body, ifLabel, elseLabel, endLabel) -> {
            MethodVisitor mv = body.getWriter();
            obj.apply(body);
            mv.visitJumpInsn(Opcodes.IFNONNULL, ifLabel);
        };
    }

    /**
     *
     * <p>ifeq equals 当栈顶int类型数值等于0时跳转</p>
     * <p>ifne not equals 当栈顶int类型数值不等于0时跳转</p>
     *
     * @param action a {@link io.github.hhy.linker.generate.bytecode.action.Action} object.
     * @return a {@link io.github.hhy.linker.generate.bytecode.action.Condition} object.
     */
    static Condition ifTrue(Action action) {
        return (body, ifLabel, elseLabel, endLabel) -> {
            action.apply(body);
            body.getWriter().visitJumpInsn(Opcodes.IFNE, ifLabel);
        };
    }

    /**
     * <p>ifFalse.</p>
     *
     * @param obj a {@link io.github.hhy.linker.generate.bytecode.action.Action} object.
     * @return a {@link io.github.hhy.linker.generate.bytecode.action.Condition} object.
     */
    public static Condition ifFalse(Action obj) {
        return (body, ifLabel, elseLabel, endLabel) -> {
            MethodVisitor mv = body.getWriter();
            obj.apply(body);
            mv.visitJumpInsn(Opcodes.IFEQ, ifLabel);
        };
    }

    /**
     * <p>notEq.</p>
     *
     * @param left a {@link io.github.hhy.linker.generate.bytecode.action.Action} object.
     * @param right a {@link io.github.hhy.linker.generate.bytecode.action.Action} object.
     * @return a {@link io.github.hhy.linker.generate.bytecode.action.Condition} object.
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
     * <p>instanceOf.</p>
     *
     * @param obj a {@link io.github.hhy.linker.generate.bytecode.action.Action} object.
     * @param expectType a {@link org.objectweb.asm.Type} object.
     * @return a {@link io.github.hhy.linker.generate.bytecode.action.Condition} object.
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
     * 组合条件: 任意条件满足
     *
     * @param conditions a {@link io.github.hhy.linker.generate.bytecode.action.Condition} object.
     * @return a {@link io.github.hhy.linker.generate.bytecode.action.Condition} object.
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
     * 组合条件: 全部满足
     *
     * @param conditions a {@link io.github.hhy.linker.generate.bytecode.action.Condition} object.
     * @return a {@link io.github.hhy.linker.generate.bytecode.action.Condition} object.
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
