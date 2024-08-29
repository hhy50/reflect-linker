package io.github.hhy.linker.generate.bytecode.action;

import io.github.hhy.linker.entity.MethodHolder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.vars.ObjectVar;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Arrays;

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

    static Action throwTypeCastException(Type realType, Type expectType) {
        return body -> {
            MethodVisitor mv = body.getWriter();
            mv.visitTypeInsn(Opcodes.NEW, "java/lang/ClassCastException");
            mv.visitInsn(Opcodes.DUP);
            mv.visitLdcInsn("type '"+realType.getClassName()+"' not cast to type '"+expectType.getClassName()+"'");
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/ClassCastException", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitInsn(Opcodes.ATHROW);
        };
    }

    static Action asList(Action... actions) {
        return body -> {
            body.append(() -> new MethodInvokeAction(MethodHolder.ARRAYS_ASLIST).setArgs(asArray(ObjectVar.TYPE, actions)));
        };
    }

    static Action asArray(Type arrType, Action... actions) {
        return body -> {
            MethodVisitor mv = body.getWriter();
            // BIPUSH, -128 <--> 127
            mv.visitIntInsn(Opcodes.BIPUSH, actions.length);
            mv.visitTypeInsn(Opcodes.ANEWARRAY, arrType.getInternalName());
            mv.visitInsn(Opcodes.DUP);

            for (int i = 0; i < actions.length; i++) {
                mv.visitIntInsn(Opcodes.BIPUSH, i);
                actions[i].apply(body);
                mv.visitInsn(arrType.getOpcode(Opcodes.IASTORE));

                if (i != actions.length-1) mv.visitInsn(Opcodes.DUP);
            }
        };
    }

    static Action asClassList(Type[] types) {
        return asList(Arrays.stream(types).map(LdcLoadAction::of).toArray(LdcLoadAction[]::new));
    }
}
