package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.MethodDefine;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.generate.AbstractDecorator;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

/**
 * <p>InvokerDecorator class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class InvokerDecorator extends AbstractDecorator {

    protected Invoker<?> realInvoker;
    private final MethodDefine methodDefine;

    /**
     * <p>Constructor for InvokerDecorator.</p>
     *
     * @param implClass a {@link java.lang.String} object.
     * @param realInvoker a {@link Invoker} object.
     * @param methodDefine a {@link MethodDefine} object.
     */
    public InvokerDecorator(String implClass, Invoker<?> realInvoker, MethodDefine methodDefine) {
        this.realInvoker = realInvoker;
        this.methodDefine = methodDefine;
    }

    /** {@inheritDoc} */
    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        this.realInvoker.define(classImplBuilder);
    }

    /** {@inheritDoc} */
    @Override
    public VarInst invoke(MethodBody methodBody) {
        MethodRef methodRef = methodDefine.methodRef;
        Type[] argsType = methodRef.getArgsType();

        typecastArgs(methodBody, methodBody.getArgs(), methodDefine.define.getParameterTypes(), argsType);
        VarInst result = realInvoker.invoke(methodBody);

        Class<?> rClassType = methodDefine.define.getReturnType();
        if (result != null && rClassType != Void.TYPE) {
//            result.ifNull(methodBody, returnNull());
            result = typecastResult(methodBody, result, rClassType);
            result.returnThis(methodBody);
        } else {
            AsmUtil.areturn(methodBody.getWriter(), Type.VOID_TYPE);
        }
        return null;
    }
}