package io.github.hhy.linker.generate.bytecode.action;

import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.runtime.RuntimeUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class WrapTypeAction implements Action {
    private final Action obj;
    private final Type type;

    public WrapTypeAction(Action obj, Type type) {
        this.obj = obj;
        this.type = type;
    }

    @Override
    public void apply(MethodBody body) {
        obj.apply(body);

        MethodVisitor mv = body.getWriter();
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.RUNTIME_UTIL_OWNER, "wrap", "("+type.getDescriptor()+")Ljava/lang/Object;", false);
    }
}