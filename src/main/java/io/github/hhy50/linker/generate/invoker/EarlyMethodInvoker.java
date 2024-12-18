package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.utils.Args;
import io.github.hhy50.linker.generate.bytecode.vars.LookupVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.getter.Getter;
import io.github.hhy50.linker.runtime.Runtime;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Arrays;

/**
 * The type Early method invoker.
 */
public class EarlyMethodInvoker extends Invoker<EarlyMethodRef> {

    /**
     * Instantiates a new Early method invoker.
     *
     * @param implClass the impl class
     * @param methodRef the method ref
     */
    public EarlyMethodInvoker(String implClass, EarlyMethodRef methodRef) {
        super(implClass, methodRef, methodRef.getMethodType());
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        FieldRef owner = method.getOwner();
        Getter<?> getter = classImplBuilder.getGetter(owner.getUniqueName());
        getter.define(classImplBuilder);

        MethodBody clinit = classImplBuilder.getClinit();

        // init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(method.getInvokerName(), method.getDeclareType(), methodType);
        initStaticMethodHandle(clinit, mhMember, loadClass(method.getDeclareType()), method.getName(), method.getMethodType(), method.isStatic());
        mhMember.setInvokeExact(!generic);

        // 定义当前方法的invoker
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodDescriptor.getMethodName(), methodDescriptor.getDesc(), null)
                .intercept((method.isStatic()
                        ? mhMember.invokeStatic(Args.loadArgs())
                        : ChainAction.of(getter::invoke).peek(VarInst::checkNullPointer).then(varInst -> mhMember.invokeInstance(varInst, Args.loadArgs())))
                        .andThen(Actions.areturn(methodType.getReturnType()))
                );
    }

    @Override
    protected void initStaticMethodHandle(MethodBody clinit, MethodHandleMember mhMember, Action lookupClass, String fieldName, Type methodType, boolean isStatic) {
        String superClass = this.method.getSuperClass();
        boolean invokeSpecial = superClass != null & !isStatic;
        VarInst lookupVar = clinit.newLocalVar(LookupVar.TYPE, new MethodInvokeAction(Runtime.LOOKUP)
                .setArgs(lookupClass));
        MethodInvokeAction findXXX;
        Action argsType = Actions.asArray(Type.getType(Class.class), Arrays.stream(methodType.getArgumentTypes()).map(LdcLoadAction::of).toArray(LdcLoadAction[]::new));
        if (invokeSpecial) {
            findXXX = new MethodInvokeAction(MethodDescriptor.LOOKUP_FIND_FINDSPECIAL).setArgs(
                    LdcLoadAction.of(AsmUtil.getType(superClass)),
                    LdcLoadAction.of(fieldName),
                    new MethodInvokeAction(MethodDescriptor.METHOD_TYPE).setArgs(LdcLoadAction.of(methodType.getReturnType()), argsType),
                    lookupClass
            );
        } else {
            findXXX = new MethodInvokeAction(isStatic ? MethodDescriptor.LOOKUP_FIND_FINDSTATIC : MethodDescriptor.LOOKUP_FIND_FINDVIRTUAL).setArgs(
                    superClass != null ? LdcLoadAction.of(AsmUtil.getType(superClass)) : lookupClass,
                    LdcLoadAction.of(fieldName),
                    new MethodInvokeAction(MethodDescriptor.METHOD_TYPE).setArgs(LdcLoadAction.of(methodType.getReturnType()), argsType)
            );
        }
        mhMember.store(clinit, findXXX.setInstance(lookupVar));
    }
}
