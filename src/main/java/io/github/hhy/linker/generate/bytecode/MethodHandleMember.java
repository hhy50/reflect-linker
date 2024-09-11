package io.github.hhy.linker.generate.bytecode;


import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.constant.MethodHandle;
import io.github.hhy.linker.entity.MethodHolder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.action.Action;
import io.github.hhy.linker.generate.bytecode.action.Condition;
import io.github.hhy.linker.generate.bytecode.action.ConditionJumpAction;
import io.github.hhy.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import io.github.hhy.linker.runtime.RuntimeUtil;
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
     * @param owner a {@link java.lang.String} object.
     * @param mhVarName a {@link java.lang.String} object.
     * @param access a int.
     * @param methodType a {@link org.objectweb.asm.Type} object.
     */
    public MethodHandleMember(int access, String owner, String mhVarName, Type methodType) {
        super(access, owner, mhVarName, MethodHandle.TYPE);
        this.methodType = methodType;
    }

    /**
     * <p>invoke.</p>
     *
     * @param methodBody a {@link io.github.hhy.linker.generate.MethodBody} object.
     * @param that a {@link io.github.hhy.linker.generate.bytecode.vars.VarInst} object.
     * @param args a {@link io.github.hhy.linker.generate.bytecode.vars.VarInst} object.
     * @return a {@link io.github.hhy.linker.generate.bytecode.vars.VarInst} object.
     */
    public VarInst invoke(MethodBody methodBody, VarInst that, VarInst... args) {
        VarInst result = initResultVar(methodBody);

        methodBody.append(() -> {
            MethodInvokeAction contains = new MethodInvokeAction(RuntimeUtil.IS_STATIC)
                    .setArgs(this);

            return new ConditionJumpAction(Condition.wrap(contains),
                    (__) -> invokeStatic(result, methodBody, args),
                    (__) -> {
                        that.checkNullPointer(methodBody, that.getName());
                        invokeInstance(result, methodBody, that, args);
                    });
        });
        return result;
    }

    /**
     * <p>invokeStatic.</p>
     *
     * @param methodBody a {@link io.github.hhy.linker.generate.MethodBody} object.
     * @param args a {@link io.github.hhy.linker.generate.bytecode.vars.VarInst} object.
     * @return a {@link io.github.hhy.linker.generate.bytecode.vars.VarInst} object.
     */
    public VarInst invokeStatic(MethodBody methodBody, VarInst... args) {
        VarInst result = initResultVar(methodBody);
        invokeStatic(result, methodBody, args);
        return result;
    }

    /**
     * <p>invokeInstance.</p>
     *
     * @param methodBody a {@link io.github.hhy.linker.generate.MethodBody} object.
     * @param that a {@link io.github.hhy.linker.generate.bytecode.vars.VarInst} object.
     * @param args a {@link io.github.hhy.linker.generate.bytecode.vars.VarInst} object.
     * @return a {@link io.github.hhy.linker.generate.bytecode.vars.VarInst} object.
     */
    public VarInst invokeInstance(MethodBody methodBody, VarInst that, VarInst... args) {
        VarInst result = initResultVar(methodBody);
        invokeInstance(result, methodBody, that, args);
        return result;
    }

    private void invokeStatic(VarInst result, MethodBody methodBody, VarInst... args) {
        methodBody.append(() ->
                // 动态签名
                new MethodInvokeAction(new MethodHolder("java/lang/invoke/MethodHandle", "invoke", methodType.getDescriptor()))
                        .setInstance(this)
                        .setArgs(args)
                        .onAfter(result == null ? Action.empty() : result.store(Action.stackTop()))
        );
    }

    private void invokeInstance(VarInst result, MethodBody methodBody, VarInst that, VarInst... args) {
        VarInst[] newArgs = new VarInst[args.length+1];
        newArgs[0] = that;
        System.arraycopy(args, 0, newArgs, 1, args.length);

        methodBody.append(() ->
                // 动态签名
                new MethodInvokeAction(new MethodHolder("java/lang/invoke/MethodHandle", "invoke", AsmUtil.addArgsDesc(methodType, Type.getType(Object.class), true).getDescriptor()))
                        .setInstance(this)
                        .setArgs(newArgs)
                        .onAfter(result == null ? Action.empty() : result.store(Action.stackTop()))
        );
    }

    private VarInst initResultVar(MethodBody methodBody) {
        VarInst objectVar = null;
        if (methodType.getReturnType().getSort() != Type.VOID) {
            objectVar = methodBody.newLocalVar(methodType.getReturnType(), "result", null);
        }
        return objectVar;
    }
}
