package io.github.hhy50.linker.generate.constructor;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.LoadAction;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.utils.Args;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.Runtime;
import io.github.hhy50.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * The type Constructor.
 */
public class Constructor extends MethodHandle {
    /**
     * The Method.
     */
    protected final EarlyMethodRef method;
    /**
     * The Method type.
     */
    protected final Type methodType;
    /**
     * The Method descriptor.
     */
    protected MethodDescriptor methodDescriptor;
    private boolean generic;

    /**
     * Instantiates a new Constructor.
     *
     * @param implClass the impl class
     * @param method    the method
     * @param mType     the m type
     */
    public Constructor(String implClass, EarlyMethodRef method, Type mType) {
        this.method = method;
        this.methodType = mType;
        this.methodDescriptor = MethodDescriptor.of(ClassUtil.className2path(implClass), "invoke_"+method.getFullName(), methodType.getDescriptor());
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        MethodBody clinit = classImplBuilder.getClinit();

        // init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(method.getInvokerName(), method.getDeclareType(), methodType);
        initStaticMethodHandle(clinit, mhMember, loadClass(method.getDeclareType()), null, method.getMethodType(), false);
        mhMember.setInvokeExact(!this.generic);

        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodDescriptor.getMethodName(), methodDescriptor.getDesc(), null)
                .intercept(mhMember.invokeStatic(Args.loadArgs()));
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        MethodInvokeAction invoker = new MethodInvokeAction(methodDescriptor)
                .setInstance(LoadAction.LOAD0)
                .setArgs(methodBody.getArgs());
        return methodBody.newLocalVar(methodType.getReturnType(), null, invoker);
    }

    @Override
    protected void mhReassign(MethodBody methodBody, ClassTypeMember lookupClass, MethodHandleMember mhMember, VarInst objVar) {
        throw new RuntimeException("not support runtime constructor");
    }

    @Override
    protected void initStaticMethodHandle(MethodBody clinit, MethodHandleMember mhMember, Action lookupClass, String args0, Type methodType, boolean args1) {
        MethodInvokeAction findConstructor = new MethodInvokeAction(MethodDescriptor.LOOKUP_FINDCONSTRUCTOR)
                .setInstance(new MethodInvokeAction(Runtime.LOOKUP).setArgs(lookupClass))
                .setArgs(lookupClass, mhMember);
        mhMember.store(clinit, findConstructor);
    }
}
