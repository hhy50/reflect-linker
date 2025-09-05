package io.github.hhy50.linker.generate.constructor;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.method.ConstructorRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.utils.Args;
import io.github.hhy50.linker.generate.invoker.Invoker;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Arrays;

import static io.github.hhy50.linker.generate.bytecode.action.Actions.of;


/**
 * The type Constructor.
 */
public class Constructor extends Invoker<ConstructorRef> {

    /**
     * Instantiates a new Constructor.
     *
     * @param constructor the constructor ref
     */
    public Constructor(ConstructorRef constructor) {
        super(constructor, constructor.getMethodType());
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        MethodBody clinit = classImplBuilder.getClinit();

        // init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(method.getInvokerName(), null, descriptor.getType());
        clinit.append(mhMember.store(
                initStaticMethodHandle(loadClass(method.getDeclareType()), null, descriptor.getType(), false))
        );


        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, descriptor.getMethodName(), descriptor.getType(), null)
                .intercept(mhMember.invokeStatic(Args.loadArgs()).thenReturn());
    }

    @Override
    protected Action initStaticMethodHandle(ClassLoadAction lookupClass, String arg0, Type methodType, boolean isStatic) {
        return new MethodInvokeAction(MethodDescriptor.LOOKUP_FINDCONSTRUCTOR)
                .setInstance(lookupClass.getLookup())
                .setArgs(lookupClass, new MethodInvokeAction(MethodDescriptor.METHOD_TYPE)
                        .setArgs(LdcLoadAction.of(Type.VOID_TYPE),
                                Actions.asArray(TypeUtil.CLASS_TYPE,
                                        Arrays.stream(methodType.getArgumentTypes()).map(LdcLoadAction::of).toArray(LdcLoadAction[]::new))

                        ));
    }
}
