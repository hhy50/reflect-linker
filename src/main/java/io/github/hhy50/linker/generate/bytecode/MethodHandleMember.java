package io.github.hhy50.linker.generate.bytecode;


import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.entity.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.Condition;
import io.github.hhy50.linker.generate.bytecode.action.ConditionJumpAction;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.RuntimeUtil;
import org.objectweb.asm.Type;

/**
 * The type Method handle member.
 */
public class MethodHandleMember extends Member {

    private final Type methodType;

    /**
     * Instantiates a new Method handle member.
     *
     * @param member     the member
     * @param methodType the method type
     */
    public MethodHandleMember(Member member, Type methodType) {
        super(member.access, member.owner, member.memberName, member.type);
        this.methodType = methodType;
    }


    /**
     * Invoke action.
     *
     * @param that the that
     * @param args the args
     * @return the action
     */
    public Action invoke(VarInst that, VarInst... args) {
        MethodInvokeAction isStatic = new MethodInvokeAction(RuntimeUtil.IS_STATIC)
                .setArgs(this);
        return new ConditionJumpAction(Condition.ifTrue(isStatic),
                invokeStatic(args),
                new ConditionJumpAction(Condition.isNull(that), Action.throwNullException(that.getName()), invokeInstance(that, args))
        );
    }

    /**
     * Invoke static action.
     *
     * @param args the args
     * @return the action
     */
    public Action invokeStatic(VarInst... args) {
        return new MethodInvokeAction(MethodDescriptor.of("java/lang/invoke/MethodHandle", "invoke", methodType.getDescriptor()))
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
    public Action invokeInstance(VarInst that, VarInst... args) {
        VarInst[] newArgs = new VarInst[args.length+1];
        newArgs[0] = that;
        System.arraycopy(args, 0, newArgs, 1, args.length);

        // 动态签名
        return new MethodInvokeAction(MethodDescriptor.of("java/lang/invoke/MethodHandle", "invoke", AsmUtil.addArgsDesc(methodType, Type.getType(Object.class), true).getDescriptor()))
                .setInstance(this)
                .setArgs(newArgs);
    }
}
