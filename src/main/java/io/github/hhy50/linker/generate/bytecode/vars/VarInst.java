package io.github.hhy50.linker.generate.bytecode.vars;


import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.builtin.TargetProvider;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.utils.Methods;
import org.objectweb.asm.Type;

import static io.github.hhy50.linker.generate.bytecode.action.Actions.loadNull;
import static io.github.hhy50.linker.generate.bytecode.action.Condition.instanceOf;
import static io.github.hhy50.linker.generate.bytecode.action.Condition.notNull;

/**
 * The type Var inst.
 */
public abstract class VarInst implements LoadAction, TypedAction {

    /**
     * The Body.
     */
    protected final MethodBody body;

    /**
     * The Type.
     */
    protected final Type type;

    /**
     * Instantiates a new Var inst.
     *
     * @param type the type
     */
    public VarInst(MethodBody body, Type type) {
        this.body = body;
        this.type = type;
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
        return "var[type=" + type.getClassName() + "]";
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
