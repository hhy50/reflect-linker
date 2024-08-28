package io.github.hhy.linker.generate.invoker;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.define.MethodDefine;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.MethodHandleDecorator;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

public class InvokerDecorator extends MethodHandleDecorator {

    protected Invoker<?> realInvoker;
    private final MethodDefine methodDefine;

    public InvokerDecorator(String implClass, Invoker<?> realInvoker, MethodDefine methodDefine) {
        this.realInvoker = realInvoker;
        this.methodDefine = methodDefine;
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        this.realInvoker.define(classImplBuilder);
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        typecastArgs(methodBody, methodBody.getArgs(), new Type[0]);

        VarInst result = realInvoker.invoke(methodBody);
        Type rType = typecastResult(methodBody, result, Type.getType(methodDefine.define.getReturnType()));
        AsmUtil.areturn(methodBody.getWriter(), rType);
        return null;
    }
}
