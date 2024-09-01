package io.github.hhy.linker.generate.invoker;

import io.github.hhy.linker.define.MethodDefine;
import io.github.hhy.linker.define.method.MethodRef;
import io.github.hhy.linker.generate.AbstractDecorator;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

public class InvokerDecorator extends AbstractDecorator {

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

        typecastArgs(methodBody, methodBody.getArgs(), methodDefine.define.getParameterTypes(), argsType);
        VarInst result = realInvoker.invoke(methodBody);

        Class<?> expectType = methodDefine.define.getReturnType();
        VarInst newVar = typecast(methodBody, result, Type.getType(expectType));
        newVar.returnThis(methodBody);

        return null;
    }
}
