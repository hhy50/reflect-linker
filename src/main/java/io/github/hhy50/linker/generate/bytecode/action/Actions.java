package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.function.Consumer;
import java.util.function.Function;


/**
 * The interface Action.
 */
public interface Actions {

    /**
     * The constant EMPTY.
     */
    Action EMPTY = (__) -> {
    };

    /**
     * Stack top action.
     *
     * @return the action
     */
    static Action stackTop() {
        return empty();
    }

    /**
     * Empty action.
     *
     * @return the action
     */
    static Action empty() {
        return EMPTY;
    }

    /**
     * Load null action.
     *
     * @return the action
     */
    static Action loadNull() {
        return (body) -> {
            MethodVisitor mv = body.getWriter();
            mv.visitInsn(Opcodes.ACONST_NULL);
        };
    }

    /**
     * Throw null exception action.
     *
     * @param nullerr the nullerr
     * @return the action
     */
    static Action throwNullException(String nullerr) {
        return body -> {
            MethodVisitor mv = body.getWriter();
            mv.visitTypeInsn(Opcodes.NEW, "java/lang/NullPointerException");
            mv.visitInsn(Opcodes.DUP);
            mv.visitLdcInsn(nullerr);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/NullPointerException", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitInsn(Opcodes.ATHROW);
        };
    }

    /**
     * Throw type cast exception action.
     *
     * @param objName    the obj name
     * @param expectType the expect type
     * @return the action
     */
    static Action throwTypeCastException(String objName, Type expectType) {
        return body -> {
            MethodVisitor mv = body.getWriter();
            mv.visitTypeInsn(Opcodes.NEW, "java/lang/ClassCastException");
            mv.visitInsn(Opcodes.DUP);
            mv.visitLdcInsn("'" + objName + "' not cast to type '" + expectType.getClassName() + "'");
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/ClassCastException", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitInsn(Opcodes.ATHROW);
        };
    }

    /**
     * Return null action.
     *
     * @return the action
     */
    static Action returnNull() {
        return body -> {
            MethodVisitor mv = body.getWriter();
            mv.visitInsn(Opcodes.ACONST_NULL);
            mv.visitInsn(Opcodes.ARETURN);
        };
    }

    /**
     * As list action.
     *
     * @param actions the actions
     * @return the action
     */
    static Action asList(Action... actions) {
        return body -> {
            body.append(new MethodInvokeAction(MethodDescriptor.ARRAYS_ASLIST).setArgs(asArray(ObjectVar.TYPE, actions)));
        };
    }

    /**
     * As array action.
     *
     * @param arrType the arr type
     * @param actions the actions
     * @return the action
     */
    static Action asArray(Type arrType, Action... actions) {
        return body -> {
            MethodVisitor mv = body.getWriter();
            // BIPUSH, -128 <--> 127
            mv.visitIntInsn(Opcodes.BIPUSH, actions.length);
            mv.visitTypeInsn(Opcodes.ANEWARRAY, arrType.getInternalName());
            for (int i = 0; i < actions.length; i++) {
                mv.visitInsn(Opcodes.DUP);
                mv.visitIntInsn(Opcodes.BIPUSH, i);
                actions[i].apply(body);
                mv.visitInsn(arrType.getOpcode(Opcodes.IASTORE));
            }
        };
    }

    /**
     * As array action.
     *
     * @param arrType         the arr type
     * @param actionsProvider the actions provider
     * @return the action
     */
    static Action asArray(Type arrType, Function<MethodBody, Action[]> actionsProvider) {
        return body -> {
            Action[] actions = actionsProvider.apply(body);
            MethodVisitor mv = body.getWriter();
            // BIPUSH, -128 <--> 127
            mv.visitIntInsn(Opcodes.BIPUSH, actions.length);
            mv.visitTypeInsn(Opcodes.ANEWARRAY, arrType.getInternalName());
            for (int i = 0; i < actions.length; i++) {
                mv.visitInsn(Opcodes.DUP);
                mv.visitIntInsn(Opcodes.BIPUSH, i);
                actions[i].apply(body);
                mv.visitInsn(arrType.getOpcode(Opcodes.IASTORE));
            }
        };
    }

    /**
     * Multi action.
     *
     * @param actions the actions
     * @return the action
     */
    static Action multi(Action... actions) {
        if (actions == null) {
            return EMPTY;
        }
        return body -> {
            for (Action action : actions) {
                action.apply(body);
            }
        };
    }

    /**
     * Nullable action.
     *
     * @param action the action
     * @return the action
     */
    static Action nullable(Action action) {
        if (action == null) {
            return EMPTY;
        }
        return action;
    }

    /**
     * Of action.
     *
     * @param action1  the action 1
     * @param consumer the consumer
     * @return the action
     */
    static Action of(Action action1, Consumer<MethodVisitor> consumer) {
        return body -> {
            body.append(action1);
            consumer.accept(body.getWriter());
        };
    }

    /**
     * Of action.
     *
     * @param action1  the action 1
     * @param action2  the action 2
     * @param consumer the consumer
     * @return the action
     */
    static Action of(Action action1, Action action2, Consumer<MethodVisitor> consumer) {
        return body -> {
            body.append(action1);
            body.append(action2);
            consumer.accept(body.getWriter());
        };
    }

    /**
     * Of action.
     *
     * @param action1  the action 1
     * @param action2  the action 2
     * @param action3  the action 3
     * @param consumer the consumer
     * @return the action
     */
    static Action of(Action action1, Action action2, Action action3, Consumer<MethodVisitor> consumer) {
        return body -> {
            body.append(action1);
            body.append(action2);
            body.append(action3);
            consumer.accept(body.getWriter());
        };
    }

    /**
     * Of action.
     *
     * @param action1  the action 1
     * @param action2  the action 2
     * @param action3  the action 3
     * @param action4  the action 4
     * @param consumer the consumer
     * @return the action
     */
    static Action of(Action action1, Action action2, Action action3, Action action4, Consumer<MethodVisitor> consumer) {
        return body -> {
            body.append(action1);
            body.append(action2);
            body.append(action3);
            body.append(action4);
            consumer.accept(body.getWriter());
        };
    }

    /**
     * Areturn action.
     *
     * @param rType the r type
     * @return the action
     */
    static Action areturn(Type rType) {
        return body -> {
            AsmUtil.areturn(body.getWriter(), rType);
        };
    }

    /**
     * Vreturn action.
     *
     * @return the action
     */
    static Action vreturn() {
        return body -> {
            AsmUtil.areturn(body.getWriter(), Type.VOID_TYPE);
        };
    }
}
