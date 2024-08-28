package io.github.hhy.linker.generate.invoker;

import io.github.hhy.linker.define.method.MethodRef;
import io.github.hhy.linker.entity.MethodHolder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.MethodHandle;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

public abstract class Invoker<T extends MethodRef> extends MethodHandle {
    protected final T method;
    protected MethodHolder methodHolder;

    public Invoker(String implClass, T method, Type methodType) {
        this.method = method;
        this.methodHolder = new MethodHolder(implClass, "invoke_"+method.getFullName(), methodType.getDescriptor());
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        return null;
    }
}
