package io.github.hhy50.linker.generate.bytecode.vars;


import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.provider.DefaultTargetProviderImpl;
import io.github.hhy50.linker.entity.MethodDescriptor;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.LoadAction;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.action.TypeCastAction;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.function.Consumer;

/**
 * The type Var inst.
 */
public abstract class VarInst implements LoadAction {

    private final MethodBody body;

    private final int lvbIndex;

    /**
     * The Type.
     */
    protected Type type;

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
     * Load to stack.
     */
    public void loadToStack() {
        load(body);
    }

    /**
     * Check null pointer.
     *
     * @param nullerr the nullerr
     */
    public void checkNullPointer(String nullerr) {
        if (type.getSort() > Type.DOUBLE) {
            body.append(this.ifNull(Action.throwNullException(nullerr)));
        }
    }

    /**
     * Check null pointer.
     *
     * @param nullerr   the nullerr
     * @param elseBlock the else block
     */
    public void checkNullPointer(String nullerr, Action elseBlock) {
        if (type.getSort() > Type.DOUBLE) {
            body.append(this.ifNull(Action.throwNullException(nullerr), elseBlock));
        }
    }

    /**
     * Gets this class.
     *
     * @return the this class
     */
    public MethodInvokeAction getThisClass() {
        return new MethodInvokeAction(MethodDescriptor.OBJECT_GET_CLASS)
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
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return "slot["+lvbIndex+",type="+type.getClassName()+"]";
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * Return this.
     */
    public void returnThis() {
        load(body);
        AsmUtil.areturn(body.getWriter(), type);
    }

    /**
     * Gets target.
     *
     * @param providerType the provider type
     * @return the target
     */
    public Action getTarget(Type providerType) {
        Type defaultType = Type.getType(DefaultTargetProviderImpl.class);
        if (this.type.equals(defaultType)) {
            return Action.multi(
                    new MethodInvokeAction(MethodDescriptor.DEFAULT_PROVIDER_GET_TARGET).setInstance(this),
                    new TypeCastAction(Action.stackTop(), providerType)
            );
        } else {
            return Action.multi(
                    new TypeCastAction(this, defaultType)
                            .onAfter(new MethodInvokeAction(MethodDescriptor.DEFAULT_PROVIDER_GET_TARGET)
                                    .setInstance(Action.stackTop())),
                    new TypeCastAction(Action.stackTop(), providerType)
            );
        }
    }
}
