package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

import static io.github.hhy50.linker.generate.bytecode.action.Actions.withVisitor;

/**
 * The type Array index.
 */
public class ArrayIndexAction extends VarInst {

    private VarInst varInst;

    private final List<Integer> indexs;

    /**
     * Instantiates a new Array index.
     *
     * @param varInst the var inst
     * @param indexs   the
     */
    public ArrayIndexAction(VarInst varInst, List<Object> indexs) {
        this.varInst = varInst;
        this.indexs = new ArrayList<>(indexs.size());
        for (Object index : indexs) {
            if (index instanceof Integer) {
                this.indexs.add((Integer) index);
            } else if (index instanceof String) {
                this.indexs.add(Integer.parseInt((String) index));
            } else {
                throw new IllegalArgumentException("Invalid index type: " + index.getClass());
            }
        }
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
        Type t = varInst.getType();
        return Type.getType(t.getDescriptor().substring(this.indexs.size()));
    }
}
