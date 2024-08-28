package io.github.hhy.linker.generate.invoker;

import io.github.hhy.linker.define.MethodDefine;
import io.github.hhy.linker.define.method.MethodRef;
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
        MethodRef methodRef = methodDefine.methodRef;
        Type[] argsType = methodRef.getArgsType();

        typecastArgs(methodBody, methodBody.getArgs(), argsType);
        VarInst result = realInvoker.invoke(methodBody);
        typecastResult(methodBody, result, methodDefine.define.getReturnType());
//
        return null;
    }
}
