package io.github.hhy.linker.generate.bytecode.vars;

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
     * @param lvbIndex a int.
     * @param type a {@link org.objectweb.asm.Type} object.
     * @param varName a {@link java.lang.String} object.
     */
    public LocalVarInst(int lvbIndex, Type type, String varName) {
        super(lvbIndex, type);
        this.varName = varName == null ? "var" + lvbIndex : varName;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return varName+"[type="+type.getClassName()+"]";
    }

    /**
     * <p>load.</p>
     */
    public void load() {

    }
}
