package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * <p>TypeCastAction class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class TypeCastAction implements Action {
    private final Action obj;
    private final Type type;

    /**
     * <p>Constructor for TypeCastAction.</p>
     *
     * @param obj a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     * @param type a {@link org.objectweb.asm.Type} object.
     */
    public TypeCastAction(Action obj, Type type) {
        this.obj = obj;
        this.type = type;
    }

    /** {@inheritDoc} */
    @Override
    public void apply(MethodBody body) {
        obj.apply(body);
        MethodVisitor mv = body.getWriter();
        mv.visitTypeInsn(Opcodes.CHECKCAST, type.getInternalName());
    }
}
