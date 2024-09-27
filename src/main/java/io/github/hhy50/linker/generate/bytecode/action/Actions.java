package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.entity.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.stream.IntStream;

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
     * Load args action.
     * @param argIndices
     * @return
     */
    static Action loadArgs(int... argIndices) {
        return body -> {
            VarInst[] args = body.getArgs();
            int[] indices = argIndices;
            if (argIndices.length == 0) {
                indices = IntStream.range(0, args.length).toArray();
            }
            for (int i = 0; i < indices.length; i++) {
                args[indices[i]].load(body);
            }
        };
    }
}
