package io.github.hhy50.linker.generate.bytecode.vars;


import io.github.hhy50.linker.define.MethodDescriptor;
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
     * The Type.
     */
    protected final Type type;

    /**
     * Instantiates a new Var inst.
     *
     * @param type the type
     */
    public VarInst(Type type) {
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
     * Check null pointer.
     *
     * @return the action
     */
    public Action checkNullPointer() {
        return this.ifNull(Actions.throwNullException(this.getName()));
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
