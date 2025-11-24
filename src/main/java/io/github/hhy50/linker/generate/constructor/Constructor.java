package io.github.hhy50.linker.generate.constructor;

import io.github.hhy50.linker.define.method.ConstructorRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.invoker.Invoker;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.function.Function;


/**
 * The type Constructor.
 */
public class Constructor extends Invoker<ConstructorRef> {
    /**
     *
     */
    private ConstructorRef method;

    /**
     * The Inline action.
     */
    public Function<ChainAction<VarInst[]>, VarInst> inlineAction;

    /**
     * Instantiates a new Constructor.
     *
     * @param constructor the constructor ref
     */
    public Constructor(ConstructorRef constructor) {
        super(null, constructor.getMhType());
        this.method = constructor;
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        MethodBody clinit = classImplBuilder.getClinit();

        // init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(method.getFullName(), null, super.lookupMhType);
        clinit.append(initStaticMethodHandle(mhMember, loadClass(method.getDeclareType()), false));
        this.inlineAction = args -> Actions.newLocalVar(mhMember.invokeStatic(args));
    }

    @Override
    public ChainAction<VarInst> invoke(ChainAction<VarInst> varInstChain, ChainAction<VarInst[]> argsChainAction) {
        return varInstChain.map(__ -> inlineAction.apply(argsChainAction));
    }

    @Override
    protected Action initStaticMethodHandle(MethodHandleMember mhMember, ClassTypeVarInst lookupClass, boolean args1) {
        return mhMember.store(new MethodInvokeAction(MethodDescriptor.LOOKUP_FINDCONSTRUCTOR)
                .setInstance(lookupClass.getLookup())
                .setArgs(lookupClass, new MethodInvokeAction(MethodDescriptor.METHOD_TYPE)
                        .setArgs(LdcLoadAction.of(Type.VOID_TYPE),
                                Actions.asArray(TypeUtil.CLASS_TYPE,
                                        Arrays.stream(lookupMhType.getArgumentTypes())
                                                .map(LdcLoadAction::of).toArray(LdcLoadAction[]::new))
                        )));
    }
}
