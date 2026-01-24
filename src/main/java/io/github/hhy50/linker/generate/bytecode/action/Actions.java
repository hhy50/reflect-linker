package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


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
        return withVisitor(mv -> mv.visitInsn(Opcodes.ACONST_NULL));
    }

    /**
     * Throw null exception action.
     *
     * @param nullerr the nullerr
     * @return the action
     */
    static Action throwNullException(String nullerr) {
        return withVisitor(mv -> {
            mv.visitTypeInsn(Opcodes.NEW, "java/lang/NullPointerException");
            mv.visitInsn(Opcodes.DUP);
            mv.visitLdcInsn(nullerr);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/NullPointerException", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitInsn(Opcodes.ATHROW);
        });
    }

    /**
     * Throw type cast exception action.
     *
     * @param objName    the obj name
     * @param expectType the expect type
     * @return the action
     */
    static Action throwTypeCastException(String objName, Type expectType) {
        return withVisitor(mv -> {
            mv.visitTypeInsn(Opcodes.NEW, "java/lang/ClassCastException");
            mv.visitInsn(Opcodes.DUP);
            mv.visitLdcInsn("'" + objName + "' not cast to type '" + expectType.getClassName() + "'");
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/ClassCastException", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitInsn(Opcodes.ATHROW);
        });
    }

    /**
     * Return null action.
     *
     * @return the action
     */
    static Action returnNull() {
        return withVisitor(mv -> {
            mv.visitInsn(Opcodes.ACONST_NULL);
            mv.visitInsn(Opcodes.ARETURN);
        });
    }

    /**
     * As list action.
     *
     * @param actions the actions
     * @return the action
     */
    static Action asList(Action... actions) {
        return new MethodInvokeAction(MethodDescriptor.ARRAYS_ASLIST).setArgs(asArray(ObjectVar.TYPE, actions));
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
     * Of action.
     *
     * @param actions the action 1
     * @return the action
     */
    static Action of(Action... actions) {
        if (actions == null) {
            return Actions.empty();
        }
        return body -> {
            for (Action action : actions) {
                if (action != null) {
                    body.append(action);
                }
            }
        };
    }

    /**
     * withVisitor action.
     *
     * @param mvApply
     * @return
     */
    static Action withVisitor(Consumer<MethodVisitor> mvApply) {
        return body -> mvApply.accept(body.getWriter());
    }

    /**
     * withWrite action.
     *
     * @param action1  the action 1
     * @param consumer the consumer
     * @return the action
     */
    static Action withVisitor(Action action1, Consumer<MethodVisitor> consumer) {
        return body -> {
            body.append(action1 == null ? EMPTY : action1);
            consumer.accept(body.getWriter());
        };
    }

    /**
     * withWrite action.
     *
     * @param action1  the action 1
     * @param action2  the action 2
     * @param consumer the consumer
     * @return the action
     */
    static Action withVisitor(Action action1, Action action2, Consumer<MethodVisitor> consumer) {
        return body -> {
            body.append(action1 == null ? EMPTY : action1);
            body.append(action2 == null ? EMPTY : action2);
            consumer.accept(body.getWriter());
        };
    }

    /**
     * withWrite action.
     *
     * @param action1  the action 1
     * @param action2  the action 2
     * @param action3  the action 3
     * @param consumer the consumer
     * @return the action
     */
    static Action withVisitor(Action action1, Action action2, Action action3, Consumer<MethodVisitor> consumer) {
        return body -> {
            body.append(action1 == null ? EMPTY : action1);
            body.append(action2 == null ? EMPTY : action2);
            body.append(action3 == null ? EMPTY : action3);
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
        return withVisitor(mv -> AsmUtil.areturn(mv, rType));
    }

    /**
     * Areturn action.
     *
     * @param rType the r type
     * @return the action
     */
    static Action areturn(Supplier<Type> rType) {
        return withVisitor(mv -> AsmUtil.areturn(mv, rType.get()));
    }

    /**
     * Vreturn action.
     *
     * @return the action
     */
    static Action vreturn() {
        return withVisitor(mv -> AsmUtil.areturn(mv, Type.VOID_TYPE));
    }

    static VarInst newLocalVar(TypedAction action) {
        return newLocalVar(action.getType(), action);
    }

    static VarInst newLocalVar(String name, TypedAction action) {
        return newLocalVar(action.getType(), name, action);
    }

    static VarInst newLocalVar(Type type, Action action) {
        return newLocalVar(type, null, action);
    }

    static VarInst newLocalVar(Type type, String name, Action action) {
        return new VarInst() {
            VarInst varInst;

            @Override
            public Type getType() {
                return type;
            }

            @Override
            public Action load() {
                return body -> {
                    if (varInst == null) {
                        varInst = body.newLocalVar(type, name, action);
                    }
                    body.append(varInst);
                };
            }
        };
    }
}
