package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

import static io.github.hhy50.linker.generate.bytecode.action.Actions.of;
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
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(varInst);

        Type type = varInst.getType();
        for (Integer i : indexs) {
            if (type.getDimensions() == 1) {
                type = type.getElementType();
            } else {
                type = Type.getType(type.getDescriptor().substring(1));
            }
            final Type fType = type;
            actions.add(withVisitor(LdcLoadAction.of(i), mv -> mv.visitInsn(fType.getOpcode(Opcodes.IALOAD))));
        }
        return of(actions.toArray(new Action[0]));
    }

    @Override
    public Type getType() {
        return varInst.getType();
    }
}
