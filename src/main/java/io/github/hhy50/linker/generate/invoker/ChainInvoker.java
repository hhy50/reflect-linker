package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.annotations.Autolink;
import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.define.method.RuntimeMethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.action.LoadAction;
import io.github.hhy50.linker.generate.bytecode.utils.Args;
import io.github.hhy50.linker.generate.bytecode.utils.Methods;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ChainInvoker extends Invoker<MethodRef> {

    /**
     * Instantiates a new Invoker.
     *
     * @param curType
     * @param method  the method
     */
    public ChainInvoker(Type curType, MethodRef method) {
        super(method, TypeUtil.appendArgs(method.getMethodType(), curType, true));
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        if (method instanceof RuntimeMethodRef) {
            defineRuntimeMethodHandle(classImplBuilder, (RuntimeMethodRef) method);
        } else {
            defineMethod(classImplBuilder, (EarlyMethodRef) method);
        }
    }

    public VarInst invokeNext(MethodBody body, VarInst varInst) {
        return body.newLocalVar(descriptor.getReturnType(), null, Methods.invoke(descriptor)
                .setInstance(LoadAction.LOAD0)
                .setArgs(varInst));
    }

    protected void defineRuntimeMethodHandle(InvokeClassImplBuilder classImplBuilder, RuntimeMethodRef method) {
        boolean autolink = method.isAutolink();
        Type mhType = descriptor.getType();
        Action args = autolink ? Actions.asArray(ObjectVar.TYPE, MethodBody::getArgs) : Args.loadArgsIgnoreThis();
        if (autolink) {
            // 因为是根据形参寻找方法，但是形参是链接器，所以找不到具体方法，查找逻辑在io.github.hhy50.linker.runtime.Runtime.findMethod
            // 约定将参数0设置为Autolink，以保证使用实参来查找方法
            mhType = Type.getMethodType(descriptor.getReturnType(), Type.getType(Object[].class));
            method.setArgsType(new Type[]{Type.getType(Autolink.class)});
        }
        ClassTypeMember lookupClass = classImplBuilder.defineLookupClass(method.getUniqueName());
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(method.getInvokerName(), mhType);

        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, descriptor.getMethodName(), descriptor.getType(), null)
                .intercept(ChainAction.of(body -> body.getArgs()[0])
                                .then((body, ownerVar) -> checkLookClass(body, lookupClass, ownerVar, null))
//                                .then((body, ownerVar) -> {
//                                    ClassTypeMember prevLookupClass = ownerGetter.lookupClass;
//                                    if (prevLookupClass != null) {
//                                        staticCheckClass(body, lookupClass, owner.fieldName, prevLookupClass);
//                                    }
//                                })
                                .then((body, ownerVar) -> checkMethodHandle(body, lookupClass, mhMember, ownerVar))
                                .map(method.isDesignateStatic() ?
                                        (method.isStatic() ? ownerVar -> mhMember.invokeStatic(args)
                                                : ownerVar -> mhMember.invokeInstance(ownerVar, args))
                                        : ownerVar -> mhMember.invokeOfNull(ownerVar, args)
                                ),
                        Actions.areturn(descriptor.getReturnType()));
    }

    protected void defineMethod(InvokeClassImplBuilder classImplBuilder, EarlyMethodRef method) {
        MethodBody clinit = classImplBuilder.getClinit();

        // init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(method.getInvokerName(), method.getLookupClass(), descriptor.getType());
        initStaticMethodHandle(clinit, mhMember, loadClass(method.getLookupClass()), method.getName(), method.getDeclareType(), method.isStatic());
//        mhMember.setInvokeExact(!method.is);

        // 定义当前方法的invoker
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, descriptor.getMethodName(), descriptor.getType(), null)
                .intercept((method.isStatic()
                        ? mhMember.invokeStatic(Args.loadArgs())
                        : ChainAction.of(body -> body.getArgs()[0]).then(VarInst::checkNullPointer).map(varInst -> mhMember.invokeStatic(Args.loadArgs())))
                        .andThen(Actions.areturn(descriptor.getReturnType()))
                );
    }
}
