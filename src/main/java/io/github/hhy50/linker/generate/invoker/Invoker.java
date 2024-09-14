package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.entity.MethodHolder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.LoadAction;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.util.ClassUtil;
import org.objectweb.asm.Type;

/**
 * <p>Abstract Invoker class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public abstract class Invoker<T extends MethodRef> extends MethodHandle {
    protected final T method;
    protected final Type methodType;
    protected MethodHolder methodHolder;

    /**
     * <p>Constructor for Invoker.</p>
     *
     * @param implClass a {@link java.lang.String} object.
     * @param method a T object.
     * @param mType a {@link org.objectweb.asm.Type} object.
     */
    public Invoker(String implClass, T method, Type mType) {
        this.method = method;
        this.methodType = genericType(mType);
        this.methodHolder = new MethodHolder(ClassUtil.className2path(implClass), "invoke_" + method.getFullName(), methodType.getDescriptor());
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    protected void mhReassign(MethodBody methodBody, VarInst lookupVar, MethodHandleMember mhMember, VarInst objVar) {

    }
}
