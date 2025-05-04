package io.github.hhy50.linker.generate.bytecode;


import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.RuntimeUtil;
import org.objectweb.asm.Type;

/**
 * The type Method handle member.
 */
public class MethodHandleMember extends Member {

    private final Type invokerType;

    private final Type methodType;

    private boolean invokeExact;

    /**
     * Instantiates a new Method handle member.
     *
     * @param member     the member
     * @param methodType the method type
     */
    public MethodHandleMember(Member member, Type methodType) {
        super(member.access, member.owner, member.memberName, member.type);
        this.invokerType = null;
        this.methodType = methodType;
    }

    /**
     * Instantiates a new Method handle member.
     *
     * @param member      the member
     * @param invokerType the invoker type
     * @param methodType  the method type
     */
    public MethodHandleMember(Member member, Type invokerType, Type methodType) {
        super(member.access, member.owner, member.memberName, member.type);
        this.invokerType = invokerType;
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
        Type invokerType = this.invokerType == null ? that.getType() : this.invokerType;

        Action[] newArgs = new Action[args.length+1];
        newArgs[0] = new TypeCastAction(that, invokerType);
        System.arraycopy(args, 0, newArgs, 1, args.length);

        // 动态签名
        return new MethodInvokeAction(MethodDescriptor.of("java/lang/invoke/MethodHandle", invokeExact ? "invokeExact" : "invoke",
                AsmUtil.addArgsDesc(methodType, invokerType, true)))
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
}
