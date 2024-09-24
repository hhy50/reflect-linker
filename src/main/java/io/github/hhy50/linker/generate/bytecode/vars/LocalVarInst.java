package io.github.hhy50.linker.generate.bytecode.vars;

import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.Type;

/**
 * <p>LocalVarInst class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class LocalVarInst extends VarInst {

    private final String varName;

    /**
     * <p>Constructor for LocalVarInst.</p>
     *
     * @param body     a MethodBody.
     * @param lvbIndex a int.
     * @param type     a {@link Type} object.
     * @param varName  a {@link String} object.
     */
    public LocalVarInst(MethodBody body, int lvbIndex, Type type, String varName) {
        super(body, lvbIndex, type);
        this.varName = varName == null ? "var" + lvbIndex : varName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return varName + "[type=" + type.getClassName() + "]";
    }

    /**
     * <p>load.</p>
     */
    public void load() {

    }
}
