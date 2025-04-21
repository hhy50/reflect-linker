package io.github.hhy50.linker.generate.bytecode.vars;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.util.TypeUtils;

/**
 * The type Class var.
 */
public class ClassVar extends VarInst{


    /**
     * Instantiates a new Class var.
     *
     * @param body     the body
     * @param lvbIndex the lvb index
     */
    public ClassVar(MethodBody body, int lvbIndex) {
        super(body, lvbIndex, TypeUtils.CLASS_TYPE);
    }
}
