package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.MethodDefine;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.generate.AbstractDecorator;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.constructor.Constructor;
import org.objectweb.asm.Type;

import java.util.Objects;

/**
 * The type Invoker decorator.
 */
public class InvokerDecorator extends AbstractDecorator {

    /**
     * The Real invoker.
     */
    protected Invoker<?> realInvoker;
    private final MethodDefine methodDefine;

    /**
     * Instantiates a new Invoker decorator.
     *
     * @param realInvoker  the real invoker
     * @param methodDefine the method define
     */
    public InvokerDecorator(Invoker<?> realInvoker, MethodDefine methodDefine) {
        super(methodDefine);
        Objects.requireNonNull(realInvoker);

        this.realInvoker = realInvoker;
        this.methodDefine = methodDefine;
        this.autolink = this.autolink || realInvoker instanceof Constructor;
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

        Class<?> rClassType = methodDefine.method.getReturnType();
        if (result != null && rClassType != Void.TYPE) {
            typecastResult(methodBody, result)
                    .returnThis();
        } else {
            AsmUtil.areturn(methodBody.getWriter(), Type.VOID_TYPE);
        }
        return null;
    }
}
