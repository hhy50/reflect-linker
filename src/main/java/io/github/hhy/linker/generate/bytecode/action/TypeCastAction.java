package io.github.hhy.linker.generate.bytecode.action;

import io.github.hhy.linker.generate.MethodBody;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class TypeCastAction implements Action {
    private final Action obj;
    private final Type type;

    public TypeCastAction(Action obj, Type type) {
        this.obj = obj;
        this.type = type;
    }

    @Override
    public void apply(MethodBody body) {
        if (obj != null) {
            obj.apply(body);
        }
        MethodVisitor mv = body.getWriter();
        mv.visitTypeInsn(Opcodes.CHECKCAST, type.getInternalName());
    }
}
