package io.github.hhy50.linker.generate.bytecode;


import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.constant.MethodHandle;
import io.github.hhy50.linker.entity.MethodHolder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.Condition;
import io.github.hhy50.linker.generate.bytecode.action.ConditionJumpAction;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.RuntimeUtil;
import org.objectweb.asm.Type;

/**
 * <p>MethodHandleMember class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class MethodHandleMember extends Member {

    private final Type methodType;

    /**
     * <p>Constructor for MethodHandleMember.</p>
     *
     * @param owner      a {@link java.lang.String} object.
     * @param mhVarName  a {@link java.lang.String} object.
     * @param access     a int.
     * @param methodType a {@link org.objectweb.asm.Type} object.
     */
    public MethodHandleMember(int access, String owner, String mhVarName, Type methodType) {
        super(access, owner, mhVarName, MethodHandle.TYPE);
        this.methodType = methodType;
    }

    /**
     * <p>invoke.</p>
     *
     * @param that       a {@link io.github.hhy50.linker.generate.bytecode.vars.VarInst} object.
     * @param args       a {@link io.github.hhy50.linker.generate.bytecode.vars.VarInst} object.
     */
    public Action invoke(VarInst that, VarInst... args) {
        MethodInvokeAction isStatic = new MethodInvokeAction(RuntimeUtil.IS_STATIC)
                .setArgs(this);
        return new ConditionJumpAction(Condition.ifTrue(isStatic),
                invokeStatic(args),
                new ConditionJumpAction(Condition.isNull(that), Action.throwNullException(that.getName()), invokeInstance(that, args))
        );
    }

    public Action invokeStatic(VarInst... args) {
        return new MethodInvokeAction(new MethodHolder("java/lang/invoke/MethodHandle", "invoke", methodType.getDescriptor()))
                .setInstance(this)
                .setArgs(args);
    }

    public Action invokeInstance(VarInst that, VarInst... args) {
        VarInst[] newArgs = new VarInst[args.length+1];
        newArgs[0] = that;
        System.arraycopy(args, 0, newArgs, 1, args.length);

        // 动态签名
        return new MethodInvokeAction(new MethodHolder("java/lang/invoke/MethodHandle", "invoke", AsmUtil.addArgsDesc(methodType, Type.getType(Object.class), true).getDescriptor()))
                .setInstance(this)
                .setArgs(newArgs);
    }
}
