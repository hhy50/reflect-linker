package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static io.github.hhy50.linker.generate.bytecode.action.Actions.of;

/**
 * The type Array index.
 */
public class ArrayIndex implements Action, TypedAction {
    private VarInst varInst;
    private final int i;

    /**
     * Instantiates a new Array index.
     *
     * @param varInst the var inst
     * @param i       the
     */
    public ArrayIndex(VarInst varInst, int i) {
        this.varInst = varInst;
        this.i = i;
    }

    @Override
    public void apply(MethodBody body) {
        Type type = varInst.getType();
        body.append(of(varInst,
                LdcLoadAction.of(i),
                (c) -> c.visitInsn(type.getOpcode(Opcodes.IALOAD))
        ));
    }

    @Override
    public Type getType() {
        return varInst.getType();
    }
}
