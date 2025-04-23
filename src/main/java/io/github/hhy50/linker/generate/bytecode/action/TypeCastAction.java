package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static java.util.Objects.requireNonNull;

/**
 * The type Type cast action.
 */
public class TypeCastAction implements TypedAction {
    private final Action obj;
    private final Type type;

    /**
     * Instantiates a new Type cast action.
     *
     * @param obj  the obj
     * @param type the type
     */
    public TypeCastAction(Action obj, Type type) {
        requireNonNull(obj);
        requireNonNull(type);

        this.obj = obj;
        this.type = type;
    }

    @Override
    public void apply(MethodBody body) {
        obj.apply(body);
        MethodVisitor mv = body.getWriter();
        mv.visitTypeInsn(Opcodes.CHECKCAST, type.getInternalName());
    }

    @Override
    public Type getType() {
        return type;
    }
}
