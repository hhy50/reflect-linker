package io.github.hhy50.linker.generate.bytecode;


import io.github.hhy50.linker.asm.AsmField;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.RuntimeUtil;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Type;

/**
 * The type Method handle member.
 */
public class MethodHandleMember extends Member {

    /**
     *
     */
    private final Type lookupType;

    /**
     *
     */
    private final Type methodType;

    /**
     *
     */
    private boolean invokeExact;

    /**
     *
     */
    private boolean inited;

    /**
     * Instantiates a new Method handle member.
     *
     * @param field      the field
     * @param methodType the method type
     */
    public MethodHandleMember(AsmField field, Type methodType) {
        super(field);
        this.lookupType = null;
        this.methodType = methodType;
    }

    /**
     * Instantiates a new Method handle member.
     *
     * @param field      the field
     * @param methodType the method type
     * @param lookupType the invoker type
     */
    public MethodHandleMember(AsmField field, Type methodType, Type lookupType) {
        super(field);
        this.lookupType = lookupType;
        this.methodType = methodType;
    }

    /**
     * Invoke action.
     *
     * @param that the that
     * @param args the args
     * @return the action
     */
    public Action invoke(VarInst that, Action... args) {
        MethodInvokeAction isStatic = new MethodInvokeAction(RuntimeUtil.IS_STATIC)
                .setArgs(this);
        return new ConditionJumpAction(Condition.ifTrue(isStatic),
                invokeStatic(args),
                new ConditionJumpAction(Condition.isNull(that), Actions.throwNullException(that.getName()), invokeInstance(that, args))
        );
    }

    /**
     * Invoke of null action.
     *
     * @param that the that
     * @param args the args
     * @return the action
     */
    public Action invokeOfNull(VarInst that, Action... args) {
        MethodInvokeAction isStatic = new MethodInvokeAction(RuntimeUtil.IS_STATIC)
                .setArgs(this);
        return new ConditionJumpAction(Condition.ifTrue(isStatic),
                invokeStatic(args),
                invokeInstance(that, args)
        );
    }

    /**
     * Invoke static action.
     *
     * @param args the args
     * @return the action
     */
    public MethodInvokeAction invokeStatic(Action... args) {
        return new MethodInvokeAction(MethodDescriptor.of("java/lang/invoke/MethodHandle", invokeExact ? "invokeExact" : "invoke", methodType))
                .setInstance(this)
                .setArgs(args);
    }

    /**
     * Invoke instance action.
     *
     * @param that the that
     * @param args the args
     * @return the action
     */
    public MethodInvokeAction invokeInstance(VarInst that, Action... args) {
        Type invokerType = this.lookupType == null ? that.getType() : this.lookupType;

        Action[] newArgs = new Action[args.length + 1];
        newArgs[0] = new TypeCastAction(that, invokerType);
        System.arraycopy(args, 0, newArgs, 1, args.length);

        // 动态签名
        return new MethodInvokeAction(MethodDescriptor.of("java/lang/invoke/MethodHandle", invokeExact ? "invokeExact" : "invoke",
                TypeUtil.appendArgs(methodType, invokerType, true)))
                .setInstance(this)
                .setArgs(newArgs);
    }

    /**
     * Sets invoke exact.
     *
     * @param invokeExact the invoke exact
     */
    public void setInvokeExact(boolean invokeExact) {
        this.invokeExact = invokeExact;
    }

    @Override
    public Action store(Action action) {
        if (this.inited) {
            return Actions.empty();
        }
        this.inited = true;
        return super.store(action);
    }
}
