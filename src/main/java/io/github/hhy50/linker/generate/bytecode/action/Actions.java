package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


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
            mv.visitLdcInsn("'"+objName+"' not cast to type '"+expectType.getClassName()+"'");
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
     * Multi action.
     *
     * @param actions the actions
     * @return the action
     */
    static Action multi(Action... actions) {
        return body -> {
            for (Action action : actions) {
                action.apply(body);
            }
        };
    }

    /**
     * Areturn action.
     *
     * @param rType the r type
     * @return the action
     */
    static Action areturn(Type rType) {
        return body ->  {
            AsmUtil.areturn(body.getWriter(), rType);
        };
    }

    /**
     * Vreturn action.
     *
     * @return the action
     */
    static Action vreturn() {
        return body ->  {
            AsmUtil.areturn(body.getWriter(), Type.VOID_TYPE);
        };
    }
}
