package io.github.hhy50.linker.generate.bytecode.vars;


import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.provider.TargetProvider;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.utils.Methods;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.function.Consumer;

import static io.github.hhy50.linker.generate.bytecode.action.Actions.loadNull;
import static io.github.hhy50.linker.generate.bytecode.action.Condition.instanceOf;
import static io.github.hhy50.linker.generate.bytecode.action.Condition.notNull;

/**
 * The type Var inst.
 */
public abstract class VarInst implements LoadAction, TypedAction {

    private final MethodBody body;

    private final int lvbIndex;

    /**
     * The Type.
     */
    protected final Type type;

    /**
     * Instantiates a new Var inst.
     *
     * @param body     the body
     * @param lvbIndex the lvb index
     * @param type     the type
     */
    public VarInst(MethodBody body, int lvbIndex, Type type) {
        this.body = body;
        this.lvbIndex = lvbIndex;
        this.type = type;
    }

    @Override
    public void load(MethodBody methodBody) {
        methodBody.append((Consumer<MethodVisitor>) mv -> mv.visitVarInsn(type.getOpcode(Opcodes.ILOAD), lvbIndex));
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    @Override
    public Type getType() {
        return type;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return "slot["+lvbIndex+",type="+type.getClassName()+"]";
    }

    /**
     * Load to stack.
     */
    public void loadToStack() {
        load(body);
    }

    /**
     * Check null pointer.
     */
    public void checkNullPointer() {
        if (type.getSort() > Type.DOUBLE) {
            body.append(this.ifNull(Actions.throwNullException(this.getName())));
        }
    }

    /**
     * Gets this class.
     *
     * @return the this class
     */
    public MethodInvokeAction getThisClass() {
        return new MethodInvokeAction(MethodDescriptor.GET_CLASS)
                .setInstance(this);
    }

    /**
     * Store.
     *
     * @param action the action
     */
    public void store(Action action) {
        action.apply(body);
        body.getWriter().visitVarInsn(type.getOpcode(Opcodes.ISTORE), lvbIndex);
    }

    /**
     * Return this.
     */
    public void returnThis() {
        thenReturn().apply(body);
    }

    /**
     * Gets target.
     *
     * @return the target
     */
    public Action getTarget() {
        return new ConditionJumpAction(
                notNull(this),
                Methods.invokeInterface(MethodDescriptor.TARGET_PROVIDER_GET_TARGET)
                        .setInstance(new TypeCastAction(this, Type.getType(TargetProvider.class)))
                , loadNull()
        );
    }

    /**
     * Gets target.
     *
     * @return the target
     */
    public Action tryGetTarget() {
        return new ConditionJumpAction(instanceOf(this, Type.getType(TargetProvider.class)), this.getTarget(), this);
    }
}
