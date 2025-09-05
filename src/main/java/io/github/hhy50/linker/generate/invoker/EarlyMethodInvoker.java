package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.getter.Getter;
import io.github.hhy50.linker.util.TypeUtil;

/**
 * The type Early method invoker.
 */
public class EarlyMethodInvoker extends Invoker<EarlyMethodRef> {

    /**
     * The Mh member.
     */
    protected MethodHandleMember mhMember;

    /**
     * Instantiates a new Early method invoker.
     *
     * @param methodRef the method ref
     */
    public EarlyMethodInvoker(EarlyMethodRef methodRef) {
        super(methodRef, TypeUtil.appendArgs(methodRef.getMethodType(), null, true));
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        FieldRef owner = method.getOwner();
        Getter getter = classImplBuilder.getGetter(owner);
        getter.define(classImplBuilder);

        MethodBody clinit = classImplBuilder.getClinit();

        // init methodHandle
        this.mhMember = classImplBuilder.defineStaticMethodHandle(method.getInvokerName(), method.getLookupClass(), descriptor.getType());
        mhMember.setInvokeExact(!method.isInvisible());
        clinit.append(mhMember.store(
                initStaticMethodHandle(loadClass(method.getLookupClass()),
                        method.getName(), method.getDeclareType(), method.isStatic())
        ));
    }


    @Override
    public ChainAction<VarInst> invoke(ChainAction<VarInst> varInstChain, Action... args) {
        return varInstChain.mapVar(varInst -> {
            // 直接内联调用 methodHandle
            if (method.isStatic()) {
                return mhMember.invokeStatic(args);
            }
            return mhMember.invokeInstance(varInst, args);
        });
    }
}
