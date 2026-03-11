package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.BiFunction;

import static io.github.hhy50.linker.generate.bytecode.action.ChainAction.mapOwnerAndArgs;

/**
 * The type Early method invoker.
 */
public class EarlyMethodInvoker extends Invoker<EarlyMethodRef> {
    private final Method reflect;
    private final boolean isInvisible;
    private final Type genericType;
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
        super(mr.getLookupClass(), mr.getName(), mr.getLookupType(), mr.getSuperClass());

        this.reflect = mr.getReflect();
        this.isInvisible = mr.isInvisible();
        this.genericType = mr.getGenericType();
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        MethodBody clinit = classImplBuilder.getClinit();
        boolean isStatic = Modifier.isStatic(this.reflect.getModifiers());

        // init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(this.reflect, super.lookupName, super.lookupClass, this.genericType);
        mhMember.setInvokeExact(!this.isInvisible);
        clinit.append(initStaticMethodHandle(mhMember, loadClass(this.lookupClass), isStatic));
        if (isStatic) {
            this.inlineAction = (varInst, args) -> mhMember.invokeStatic(args);
        } else {
            this.inlineAction = mhMember::invokeInstance;
        }
    }


    @Override
    public ChainAction<VarInst> invoke(ChainAction<VarInst[]> argsAction) {
        return mapOwnerAndArgs(argsAction, this.inlineAction);
    }
}
