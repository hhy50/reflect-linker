package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.RuntimeUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * The type Wrap type action.
 */
public class WrapTypeAction implements Action {
    private final VarInst obj;

    /**
     * Instantiates a new Wrap type action.
     *
     * @param obj the obj
     */
    public WrapTypeAction(VarInst obj) {
        this.obj = obj;
    }

    @Override
    public void apply(MethodBody body) {
        obj.load(body);

        MethodVisitor mv = body.getWriter();
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.OWNER, "wrap", "("+obj.getType().getDescriptor()+")Ljava/lang/Object;", false);
    }
}
