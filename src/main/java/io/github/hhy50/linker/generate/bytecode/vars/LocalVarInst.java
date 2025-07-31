package io.github.hhy50.linker.generate.bytecode.vars;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.function.Consumer;

/**
 * The type Local var inst.
 */
public class LocalVarInst extends VarInst {
    /**
     * The Body.
     */
    protected final MethodBody body;

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
        super(type);
        this.body = body;
        this.lvbIndex = lvbIndex;
        this.varName = varName == null ? "var" + lvbIndex : varName;
    }

    @Override
    public String getName() {
        return varName + "[type=" + type.getClassName() + ", local=" + lvbIndex + "]";
    }

    @Override
    public void load(MethodBody methodBody) {
        methodBody.append((Consumer<MethodVisitor>) mv -> mv.visitVarInsn(type.getOpcode(Opcodes.ILOAD), lvbIndex));
    }

    /**
     * Load to stack.
     */
    public void loadToStack() {
        load(body);
    }

    /**
     * Store.
     *
     * @param action the action
     */
    public void store(Action action) {
        body.append(Actions.of(
                action,
                mv -> mv.visitVarInsn(type.getOpcode(Opcodes.ISTORE), lvbIndex)
        ));
    }
}
