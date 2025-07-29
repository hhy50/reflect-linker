package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.List;

import static io.github.hhy50.linker.generate.bytecode.action.Actions.of;

/**
 * The type Array index.
 */
public class ArrayIndex implements Action, TypedAction {
    private VarInst varInst;
    private final List<Integer> index;

    /**
     * Instantiates a new Array index.
     *
     * @param varInst the var inst
     * @param index   the
     */
    public ArrayIndex(VarInst varInst, List<Integer> index) {
        this.varInst = varInst;
        this.index = index;
    }

    @Override
    public void apply(MethodBody body) {
        Type type = varInst.getType();
        body.append(varInst);

        for (Integer i : index) {
            if (type.getDimensions() == 1) {
                type = type.getElementType();
            } else {
                type = Type.getType(type.getDescriptor().substring(1));
            }
            final Type fType = type;
            body.append(of(LdcLoadAction.of(i),
                    mv -> mv.visitInsn(fType.getOpcode(Opcodes.IALOAD))
            ));
        }
    }

    @Override
    public Type getType() {
        return varInst.getType();
    }
}
