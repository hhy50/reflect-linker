package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.entity.MethodHolder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * <p>Action interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public interface Action {

    /**
     * Constant <code>EMPTY</code>
     */
    Action EMPTY = (__) -> {
    };

    /**
     * <p>stackTop.</p>
     *
     * @return a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     */
    static Action stackTop() {
        return empty();
    }

    /**
     * <p>empty.</p>
     *
     * @return a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     */
    static Action empty() {
        return EMPTY;
    }

    /**
     * <p>loadNull.</p>
     *
     * @return a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     */
    static Action loadNull() {
        return (body) -> {
            MethodVisitor mv = body.getWriter();
            mv.visitInsn(Opcodes.ACONST_NULL);
        };
    }

    /**
     * <p>apply.</p>
     *
     * @param body a {@link io.github.hhy50.linker.generate.MethodBody} object.
     */
    void apply(MethodBody body);

    /**
     * <p>onAfter.</p>
     *
     * @param after a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     * @return a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     */
    default Action onAfter(Action after) {
        return (body) -> {
            apply(body);
            after.apply(body);
        };
    }

    /**
     * <p>throwNullException.</p>
     *
     * @param nullerr a {@link java.lang.String} object.
     * @return a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
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
     * <p>throwTypeCastException.</p>
     *
     * @param objName    a {@link java.lang.Object} object.
     * @param expectType a {@link org.objectweb.asm.Type} object.
     * @return a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     */
    static Action throwTypeCastException(String objName, Type expectType) {
        return body -> {
            MethodVisitor mv = body.getWriter();
            mv.visitTypeInsn(Opcodes.NEW, "java/lang/ClassCastException");
            mv.visitInsn(Opcodes.DUP);
            mv.visitLdcInsn("'"+objName+"' not cast to type '"+expectType.getClassName()+"'");
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/ClassCastException", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitInsn(Opcodes.ATHROW);
        };
    }

    /**
     * <p>returnNull.</p>
     *
     * @return a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     */
    static Action returnNull() {
        return body -> {
            MethodVisitor mv = body.getWriter();
            mv.visitInsn(Opcodes.ACONST_NULL);
            mv.visitInsn(Opcodes.ARETURN);
        };
    }

    /**
     * <p>asList.</p>
     *
     * @param actions a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     * @return a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     */
    static Action asList(Action... actions) {
        return body -> {
            body.append(() -> new MethodInvokeAction(MethodHolder.ARRAYS_ASLIST).setArgs(asArray(ObjectVar.TYPE, actions)));
        };
    }

    /**
     * <p>asArray.</p>
     *
     * @param arrType a {@link org.objectweb.asm.Type} object.
     * @param actions a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     * @return a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
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
     * <p>multi.</p>
     *
     * @param actions a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     * @return a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     */
    static Action multi(Action... actions) {
        return body -> {
            for (Action action : actions) {
                action.apply(body);
            }
        };
    }
}
