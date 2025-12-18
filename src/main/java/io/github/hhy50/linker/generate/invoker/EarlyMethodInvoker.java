package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;

import java.util.function.BiFunction;

import static io.github.hhy50.linker.generate.bytecode.action.ChainAction.mapOwnerAndArgs;

/**
 * The type Early method invoker.
 */
public class EarlyMethodInvoker extends Invoker<EarlyMethodRef> {
    private String fullName;
    private boolean isStatic;
    private boolean isInvisible;

    /**
     * 内联方法调用。父类的invoke是调用这个 mh的单独生成的方法
     */
    protected BiFunction<VarInst, VarInst[], VarInst> inlineAction;

    /**
     * Instantiates a new Early method invoker.
     *
     * @param mr the method ref
     */
    public EarlyMethodInvoker(EarlyMethodRef mr) {
        super(mr.getName(), mr.getMhType());
        this.fullName = mr.getFullName().replace('.', '_');
        this.lookupClass = mr.getDeclareType();
        this.isStatic = mr.isStatic();
        this.isInvisible = mr.isInvisible();
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        MethodBody clinit = classImplBuilder.getClinit();

        // init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(this.fullName, super.lookupClass, super.lookupMhType);
        mhMember.setInvokeExact(!this.isInvisible);
        clinit.append(initStaticMethodHandle(mhMember, loadClass(this.lookupClass), isStatic));
        this.inlineAction = (varInst, args) ->
                Actions.newLocalVar(isStatic
                        ? mhMember.invokeStatic(args)
                        : mhMember.invokeInstance(varInst, args));
    }


    @Override
    public ChainAction<VarInst> invoke(ChainAction<VarInst[]> argsAction) {
        return mapOwnerAndArgs(argsAction, this.inlineAction);
    }
}
