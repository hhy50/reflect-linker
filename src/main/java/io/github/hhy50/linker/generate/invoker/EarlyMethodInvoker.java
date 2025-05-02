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
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.getter.Getter;
import io.github.hhy50.linker.util.TypeUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Arrays;

/**
 * The type Early method invoker.
 */
public class EarlyMethodInvoker extends Invoker<EarlyMethodRef> {

    /**
     * The generic.
     */
    protected boolean generic;

    /**
     * Instantiates a new Early method invoker.
     *
     * @param implClass the impl class
     * @param methodRef the method ref
     */
    public EarlyMethodInvoker(String implClass, EarlyMethodRef methodRef) {
        super(implClass, methodRef, methodRef.getMethodType());
        this.generic = methodRef.isInvisible();
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        FieldRef owner = method.getOwner();
        Getter getter = classImplBuilder.getGetter(owner.getUniqueName());
        getter.define(classImplBuilder);

        MethodBody clinit = classImplBuilder.getClinit();

        // init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(method.getInvokerName(), method.getLookupClass(), descriptor.getType());
        initStaticMethodHandle(clinit, mhMember, loadClass(method.getLookupClass()), method.getName(), method.getDeclareType(), method.isStatic());
        mhMember.setInvokeExact(!this.generic);

        // 定义当前方法的invoker
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, descriptor.getMethodName(), descriptor.getType(), null)
                .intercept((method.isStatic()
                        ? mhMember.invokeStatic(Args.loadArgs())
                        : ChainAction.of(getter::invoke).peek(VarInst::checkNullPointer).then(varInst -> mhMember.invokeInstance(varInst, Args.loadArgs())))
                        .andThen(Actions.areturn(descriptor.getReturnType()))
                );
    }

    @Override
    protected void initStaticMethodHandle(MethodBody clinit, MethodHandleMember mhMember, ClassLoadAction lookupClass, String methodName, Type methodType, boolean isStatic) {
        String superClass = this.method.getSuperClass();
        boolean invokeSpecial = superClass != null & !isStatic;
        MethodInvokeAction findXXX;
        Action argsType = Actions.asArray(TypeUtils.CLASS_TYPE, Arrays.stream(methodType.getArgumentTypes()).map(this::loadClass).toArray(Action[]::new));
        Action returnType = methodType.getReturnType() == Type.VOID_TYPE || AsmUtil.isPrimitiveType(methodType.getReturnType())
                ? LdcLoadAction.of(methodType.getReturnType()) : this.loadClass(methodType.getReturnType());
        if (invokeSpecial) {
            findXXX = new MethodInvokeAction(MethodDescriptor.LOOKUP_FINDSPECIAL).setArgs(
                    LdcLoadAction.of(AsmUtil.getType(superClass)),
                    LdcLoadAction.of(methodName),
                    new MethodInvokeAction(MethodDescriptor.METHOD_TYPE).setArgs(returnType, argsType),
                    lookupClass
            );
        } else {
            findXXX = new MethodInvokeAction(isStatic ? MethodDescriptor.LOOKUP_FINDSTATIC : MethodDescriptor.LOOKUP_FINDVIRTUAL).setArgs(
                    superClass != null ? LdcLoadAction.of(AsmUtil.getType(superClass)) : lookupClass,
                    LdcLoadAction.of(methodName),
                    new MethodInvokeAction(MethodDescriptor.METHOD_TYPE).setArgs(returnType, argsType)
            );
        }
        mhMember.store(clinit, findXXX.setInstance(lookupClass.getLookup()));
    }
}
