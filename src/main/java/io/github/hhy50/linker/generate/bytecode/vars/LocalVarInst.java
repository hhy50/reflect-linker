package io.github.hhy50.linker.generate.bytecode.vars;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.Actions;

import static io.github.hhy50.linker.generate.bytecode.action.Actions.withVisitor;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * The type Local var inst.
 */
public class LocalVarInst extends VarInst {
    /**
     * The Body.
     */
    protected final MethodBody body;

    private final Type type;

    private final int lvbIndex;

    private final String varName;

    /**
     * Instantiates a new Local var inst.
     *
     * @param body     the body
     * @param lvbIndex the lvb index
     * @param type     the type
     * @param varName  the var name
     */
    public LocalVarInst(MethodBody body, int lvbIndex, Type type, String varName) {
        this.body = body;
        this.lvbIndex = lvbIndex;
        this.type = type;
        this.varName = varName == null ? "var" + lvbIndex : varName;
    }

    @Override
    public String getName() {
        return varName + "[type=" + type.getClassName() + ", local=" + lvbIndex + "]";
    }

    @Override
    public Action load() {
        return withVisitor(mv -> mv.visitVarInsn(type.getOpcode(Opcodes.ILOAD), lvbIndex));
    }

    @Override
    public Type getType() {
        return type;
    }

    /**
     * Load to stack.
     */
    public void loadToStack() {
        body.append(this);
    }

    /**
     * Store.
     *
     * @param action the action
     */
    public void store(Action action) {
        body.append(Actions.of(
                action,
                withVisitor(mv -> mv.visitVarInsn(type.getOpcode(Opcodes.ISTORE), lvbIndex))
        ));
    }
}
