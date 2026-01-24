package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static java.util.Objects.requireNonNull;

/**
 * The type Type cast action.
 */
public class TypeCastAction extends VarInst {
    private final Action obj;
    private final Type type;

    /**
     * Instantiates a new Type cast action.
     *
     * @param obj  the obj
     * @param type the type
     */
    public TypeCastAction(VarInst obj, Type type) {
        requireNonNull(obj);
        requireNonNull(type);

        this.obj = obj;
        this.type = type;
    }

    @Override
    public Action load() {
        return Actions.withVisitor(
                obj,
                mv -> mv.visitTypeInsn(Opcodes.CHECKCAST, type.getInternalName())
        );
    }

    @Override
    public Type getType() {
        return type;
    }
}
