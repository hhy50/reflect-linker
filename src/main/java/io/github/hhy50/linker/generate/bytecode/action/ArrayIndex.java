package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.List;

import static io.github.hhy50.linker.generate.bytecode.action.Actions.withVisitor;

/**
 * The type Array index.
 */
public class ArrayIndex implements LoadAction, TypedAction {
    private VarInst varInst;
    private final List<Integer> indexs;

    /**
     * Instantiates a new Array index.
     *
     * @param varInst the var inst
     * @param indexs   the
     */
    public ArrayIndex(VarInst varInst, List<Integer> indexs) {
        this.varInst = varInst;
        this.indexs = indexs;
    }

    @Override
    public Action load() {
        Action action = varInst;

        Type type = varInst.getType();
        for (Integer i : indexs) {
            if (type.getDimensions() == 1) {
                type = type.getElementType();
            } else {
                type = Type.getType(type.getDescriptor().substring(1));
            }
            final Type fType = type;
            action = action.andThen(withVisitor(LdcLoadAction.of(i), mv -> mv.visitInsn(fType.getOpcode(Opcodes.IALOAD))));
        }
        return action;
    }

    @Override
    public Type getType() {
        return varInst.getType();
    }
}
