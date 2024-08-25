package io.github.hhy.linker.generate.bytecode.action;

import io.github.hhy.linker.generate.MethodBody;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 *
 */
public interface Action {

    void apply(MethodBody body);

    default Action onAfter(Action after) {
        return (body) -> {
            apply(body);
            after.apply(body);
        };
    }

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

    static Action wrap(Runnable runnable) {
        return __ -> runnable.run();
    }
}
