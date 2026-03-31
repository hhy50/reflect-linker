package io.github.hhy50.linker.generate.constructor;

import io.github.hhy50.linker.define.method.ConstructorRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.ClassTypeVarInst;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.function.Function;


/**
 * The type Constructor.
 */
public class Constructor extends MethodHandle {
    /**
     *
     */
    private final java.lang.reflect.Constructor<?> reflect;
    /**
     *
     */
    private final Type methodType;

    private final Type lookupType;

    /**
     * The Inline action.
     */
    public Function<VarInst[], VarInst> inlineAction;

    /**
     * Instantiates a new Constructor.
     *
     * @param constructor the constructor ref
     */
    public Constructor(ConstructorRef constructor) {
        this.reflect = constructor.getReflect();
        this.methodType = constructor.getMethodType();
        this.lookupType = Type.getType(reflect.getDeclaringClass());
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        MethodBody clinit = classImplBuilder.getClinit();

        // init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(this.reflect, "constructor", null, this.methodType);
        clinit.append(initStaticMethodHandle(mhMember, loadClass(this.lookupType), false));
        this.inlineAction = mhMember::invokeStatic;
    }

    @Override
    public ChainAction<VarInst> invoke(ChainAction<VarInst[]> argsAction) {
        // 忽略传递过来的target
        return ChainAction.mapOwnerAndArgs(argsAction, (__, args) -> inlineAction.apply(args));
    }

    @Override
    protected Action initStaticMethodHandle(MethodHandleMember mhMember, ClassTypeVarInst lookupClass, boolean args1) {
        return mhMember.store(new MethodInvokeAction(MethodDescriptor.LOOKUP_FINDCONSTRUCTOR)
                .setInstance(lookupClass.getLookup())
                .setArgs(lookupClass, new MethodInvokeAction(MethodDescriptor.METHOD_TYPE)
                        .setArgs(LdcLoadAction.of(Type.VOID_TYPE),
                                Actions.asArray(TypeUtil.CLASS_TYPE,
                                        Arrays.stream(this.methodType.getArgumentTypes())
                                                .map(LdcLoadAction::of).toArray(LdcLoadAction[]::new))
                        )));
    }
}
