package io.github.hhy50.linker.generate.constructor;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.method.ConstructorRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.utils.Args;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.invoker.Invoker;
import io.github.hhy50.linker.util.TypeUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Arrays;


/**
 * The type Constructor.
 */
public class Constructor extends Invoker<ConstructorRef> {

    /**
     * Instantiates a new Constructor.
     *
     * @param implClass the impl class
     * @param constructor    the constructor ref
     */
    public Constructor(String implClass, ConstructorRef constructor) {
        super(implClass, constructor, constructor.getMethodType());
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        MethodBody clinit = classImplBuilder.getClinit();

        // init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(method.getInvokerName(), null, descriptor.getType());
        initStaticMethodHandle(clinit, mhMember, loadClass(method.getDeclareType()), null, descriptor.getType(), false);

        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, descriptor.getMethodName(), descriptor.getType(), null)
                .intercept(mhMember.invokeStatic(Args.loadArgs()).thenReturn());
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        MethodInvokeAction invoker = new MethodInvokeAction(descriptor)
                .setInstance(LoadAction.LOAD0)
                .setArgs(methodBody.getArgs());
        return methodBody.newLocalVar(invoker);
    }

    @Override
    protected void mhReassign(MethodBody methodBody, ClassTypeMember lookupClass, MethodHandleMember mhMember, VarInst objVar) {
        throw new RuntimeException("not support runtime constructor");
    }

    @Override
    protected void initStaticMethodHandle(MethodBody clinit, MethodHandleMember mhMember, ClassLoadAction lookupClass, String args0, Type methodType, boolean args1) {
        MethodInvokeAction findConstructor = new MethodInvokeAction(MethodDescriptor.LOOKUP_FINDCONSTRUCTOR)
                .setInstance(lookupClass.getLookup())
                .setArgs(lookupClass, new MethodInvokeAction(MethodDescriptor.METHOD_TYPE)
                        .setArgs(LdcLoadAction.of(Type.VOID_TYPE),
                                Actions.asArray(TypeUtils.CLASS_TYPE,
                                        Arrays.stream(methodType.getArgumentTypes()).map(LdcLoadAction::of).toArray(LdcLoadAction[]::new))

                        ));
        mhMember.store(clinit, findConstructor);
    }
}
