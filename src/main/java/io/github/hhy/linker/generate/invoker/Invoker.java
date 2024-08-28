package io.github.hhy.linker.generate.invoker;

import io.github.hhy.linker.define.method.MethodRef;
import io.github.hhy.linker.entity.MethodHolder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.MethodHandle;
import io.github.hhy.linker.generate.bytecode.action.LoadAction;
import io.github.hhy.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

public abstract class Invoker<T extends MethodRef> extends MethodHandle {
    protected final T method;
    protected final Type methodType;
    protected MethodHolder methodHolder;

    public Invoker(String implClass, T method, Type methodType) {
        this.method = method;
        this.methodType = methodType;
        this.methodHolder = new MethodHolder(implClass, "invoke_" + method.getFullName(), methodType.getDescriptor());
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        // Object a = get_a();
        MethodInvokeAction invoker = new MethodInvokeAction(methodHolder)
                .setInstance(LoadAction.LOAD0);
        return methodBody.newLocalVar(methodType.getReturnType(), null, invoker);
    }
}
