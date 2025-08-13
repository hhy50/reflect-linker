package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.utils.Args;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.getter.Getter;
import org.objectweb.asm.Type;

/**
 * The type Early method invoker.
 */
public class EarlyMethodInvoker extends Invoker<EarlyMethodRef> {

    /**
     * The generic.
     */
    protected boolean generic;

    /**
     * 内联方法调用。父类的invoke是调用这个 mh的单独生成的方法
     */
    protected Action inlineAction;

    /**
     * Instantiates a new Early method invoker.
     *
     * @param methodRef the method ref
     */
    public EarlyMethodInvoker(EarlyMethodRef methodRef) {
        super(methodRef, methodRef.getMethodType());
        this.generic = methodRef.isInvisible();
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        FieldRef owner = method.getOwner();
        Getter getter = classImplBuilder.getGetter(owner);
        getter.define(classImplBuilder);

        MethodBody clinit = classImplBuilder.getClinit();

        // init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(method.getInvokerName(), method.getLookupClass(), descriptor.getType());
        mhMember.setInvokeExact(!this.generic);
        initStaticMethodHandle(clinit, mhMember, loadClass(method.getLookupClass()), method.getName(), method.getDeclareType(), method.isStatic());

        this.inlineAction = method.isStatic()
                ? mhMember.invokeStatic(Args.loadArgs())
                : ChainAction.of(getter::invoke)
                .map(varInst -> mhMember.invokeInstance(varInst, Args.loadArgs()));
    }


    @Override
    public VarInst invoke(MethodBody methodBody) {
        Type rType = methodBody.getMethodBuilder().getDescriptor().getReturnType();
        if (rType.getSort() == Type.VOID) {
            methodBody.append(inlineAction);
            return null;
        } else {
            return methodBody.newLocalVar(descriptor.getReturnType(), null, inlineAction);
        }
    }
}
