package io.github.hhy.linker.generate.invoker;

import io.github.hhy.linker.define.method.MethodRef;
import io.github.hhy.linker.entity.MethodHolder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.MethodHandle;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.action.LoadAction;
import io.github.hhy.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Type;

public abstract class Invoker<T extends MethodRef> extends MethodHandle {
    protected final T method;
    protected final Type methodType;
    protected MethodHolder methodHolder;

    public Invoker(String implClass, T method, Type mType) {
        this.method = method;
        this.methodType = genericType(mType);
        this.methodHolder = new MethodHolder(ClassUtil.className2path(implClass), "invoke_" + method.getFullName(), methodType.getDescriptor());
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        // Object a = get_a();
        MethodInvokeAction invoker = new MethodInvokeAction(methodHolder)
                .setInstance(LoadAction.LOAD0)
                .setArgs(methodBody.getArgs());

        Type rType = methodType.getReturnType();
        if (rType.getSort() == Type.VOID) {
            methodBody.append(() -> invoker);
            return null;
        } else {
            return methodBody.newLocalVar(methodType.getReturnType(), null, invoker);
        }
    }

    @Override
    protected void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, VarInst objVar) {

    }
}
