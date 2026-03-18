package io.github.hhy50.linker.generate.bytecode.vars;


import io.github.hhy50.linker.generate.builtin.TargetProvider;
import io.github.hhy50.linker.generate.bytecode.MethodDescriptor;
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
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return "var[type=" + this.getType().getClassName() + "]";
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
     * Is local var boolean.
     *
     * @return the boolean
     */
    public boolean isLocalVar() {
        return this instanceof LocalVarInst;
    }

    /**
     * Cast var inst.
     *
     * @param casttype the casttype
     * @return the var inst
     */
    public VarInst cast(Type casttype) {
        return new TypeCastAction(this, casttype);
    }

    /**
     * Gets target.
     *
     * @return the target
     */
    public Action tryGetTarget() {
        return new ConditionJumpAction(instanceOf(this, Type.getType(TargetProvider.class)), this.getTarget(), this);
    }

    /**
     * Wrap var inst.
     *
     * @param action the action
     * @param type   the type
     * @return the var inst
     */
    public static VarInst wrap(Action action, Type type) {
        return new VarInst() {

            @Override
            public Type getType() {
                return type;
            }

            @Override
            public Action load() {
                return action;
            }
        };
    }

    /**
     * Wrap var inst.
     *
     * @param action the action
     * @return the var inst
     */
    public static VarInst wrap(TypedAction action) {
        return new VarInst() {

            @Override
            public Type getType() {
                return action.getType();
            }

            @Override
            public Action load() {
                return action;
            }
        };
    }
}
