package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.MethodHandleFieldVarInst;
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
public class EarlyMethodInvoker extends Invoker {
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
        super(mr.getName(), Type.getType(mr.getReflect()), mr.getSuperClass());

        this.reflect = mr.getReflect();
        this.isInvisible = mr.isInvisible();
        this.genericType = mr.getMethodType();
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        MethodBody clinit = classImplBuilder.getClinit();
        Type lookupClass = Type.getType(this.reflect.getDeclaringClass());
        boolean isStatic = Modifier.isStatic(this.reflect.getModifiers());

        // init methodHandle
        MethodHandleFieldVarInst mhFieldVarInst = classImplBuilder.defineStaticMethodHandle(this.reflect, super.lookupName, lookupClass, this.genericType);
        mhFieldVarInst.setInvokeExact(!this.isInvisible);
        clinit.append(initStaticMethodHandle(mhFieldVarInst, loadClass(lookupClass), isStatic));
        if (isStatic) {
            this.inlineAction = (varInst, args) -> mhFieldVarInst.invokeStatic(args);
        } else {
            this.inlineAction = mhFieldVarInst::invokeInstance;
        }
    }


    @Override
    public ChainAction<VarInst> invoke(ChainAction<VarInst[]> argsAction) {
        return mapOwnerAndArgs(argsAction, this.inlineAction);
    }
}
