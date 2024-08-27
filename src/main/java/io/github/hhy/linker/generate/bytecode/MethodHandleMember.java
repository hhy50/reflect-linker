package io.github.hhy.linker.generate.bytecode;


import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.constant.MethodHandle;
import io.github.hhy.linker.entity.MethodHolder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.action.Condition;
import io.github.hhy.linker.generate.bytecode.action.ConditionJumpAction;
import io.github.hhy.linker.generate.bytecode.action.LdcLoadAction;
import io.github.hhy.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

public class MethodHandleMember extends Member {

    private final Type methodType;

    /**
     * @param owner
     * @param mhVarName
     */
    public MethodHandleMember(int access, String owner, String mhVarName, Type methodType) {
        super(access, owner, mhVarName, MethodHandle.TYPE);
        this.methodType = methodType;
    }

    public VarInst invoke(MethodBody methodBody, VarInst that, VarInst... args) {
        VarInst result = initResultVar(methodBody);

        methodBody.append(() -> {
            MethodInvokeAction contains = new MethodInvokeAction(MethodHolder.STRING_CONTAINS)
                    .setInstance(new MethodInvokeAction(MethodHolder.CLASS_GET_NAME)
                            .setInstance(new MethodInvokeAction(MethodHolder.OBJECT_GET_CLASS)
                                    .setInstance(this)))
                    .setArgs(LdcLoadAction.of("DirectMethodHandle$StaticAccessor"));

            return new ConditionJumpAction(Condition.wrap(contains),
                    (__) -> invokeStatic(result, methodBody, args),
                    (__) -> {
                        that.checkNullPointer(methodBody, that.getName());
                        invokeInstance(result, methodBody, that, args);
                    });
        });
        return result;
    }

    public VarInst invokeStatic(MethodBody methodBody, VarInst... args) {
        VarInst result = initResultVar(methodBody);
        invokeStatic(result, methodBody, args);
        return result;
    }

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
                        .onAfter((__) -> {
                            if (result != null) {
                                result.store(methodBody);
                            }
                        })
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
                        .onAfter((__) -> {
                            if (result != null) {
                                result.store(methodBody);
                            }
                        })
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
