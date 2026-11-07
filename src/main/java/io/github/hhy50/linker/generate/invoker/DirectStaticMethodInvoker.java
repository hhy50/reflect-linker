package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.utils.Methods;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;

import java.lang.reflect.Method;

import static io.github.hhy50.linker.generate.bytecode.action.ChainAction.mapOwnerAndArgs;

/**
 * public 静态方法的直接调用：生成 invokestatic 指令，不需要 MethodHandle。
 */
public class DirectStaticMethodInvoker extends MethodHandle {

    private final Method reflect;

    /**
     * Instantiates a new direct static method invoker.
     *
     * @param reflect the method
     */
    public DirectStaticMethodInvoker(Method reflect) {
        this.reflect = reflect;
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        // invokestatic 直接引用常量池，无需额外成员
    }

    @Override
    public ChainAction<VarInst> invoke(ChainAction<VarInst[]> argsAction) {
        return mapOwnerAndArgs(argsAction, (owner, args) ->
                Methods.invokerStatic(MethodDescriptor.of(reflect)).setArgs(args));
    }
}
