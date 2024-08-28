package io.github.hhy.linker.generate.bytecode.action;

import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import io.github.hhy.linker.runtime.RuntimeUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class WrapTypeAction implements Action {
    private final VarInst obj;

    public WrapTypeAction(VarInst obj) {
        this.obj = obj;
    }

    @Override
    public void apply(MethodBody body) {
        obj.load(body);

        MethodVisitor mv = body.getWriter();
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.RUNTIME_UTIL_OWNER, "wrap", "("+obj.getType().getDescriptor()+")Ljava/lang/Object;", false);
    }
}