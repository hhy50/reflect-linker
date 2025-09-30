package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.SmartMethodDescriptor;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.define.method.RuntimeMethodRef;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.Runtime;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.Optional;

/**
 * The type Invoker.
 *
 * @param <T> the type parameter
 */
public abstract class Invoker<T extends MethodRef> extends MethodHandle {
    /**
     * The Method.
     */
    protected final T method;

    /**
     * The Method holder.
     */
    protected final MethodDescriptor descriptor;

    /**
     * Instantiates a new Invoker.
     *
     * @param method the method
     * @param mType  the m type
     */
    public Invoker(T method, Type mType) {
        this("invoke_"+method.getFullName().replace(".", "/"), method, mType);
    }

    /**
     * Instantiates a new Invoker.
     *
     * @param mName the mName
     * @param method the method
     * @param mType  the m type
     */
    public Invoker(String mName, T method, Type mType) {
        this.method = method;
        this.descriptor = new SmartMethodDescriptor(mName, mType);
    }

    @Override
    protected Action initRuntimeMethodHandle(MethodHandleMember mhMember, ClassTypeMember lookupClass, VarInst objVar) {
        RuntimeMethodRef runtime = (RuntimeMethodRef) method;
        Class<Action> __ = Action.class;
        Action superClassLoad = Optional.ofNullable(method.getSuperClass())
                .map(LdcLoadAction::of)
                .map(__::cast)
                .orElseGet(Actions::loadNull);
        MethodInvokeAction fineMethod = new MethodInvokeAction(Runtime.FIND_METHOD)
                .setArgs(lookupClass.getLookup(), lookupClass,
                        LdcLoadAction.of(method.getName()),
                        superClassLoad,
                        Actions.asArray(TypeUtil.STRING_TYPE, Arrays.stream(runtime.getArgsType())
                                .map(Type::getClassName).map(LdcLoadAction::of).toArray(Action[]::new))
                );
        return mhMember.store(fineMethod);
    }

    @Override
    protected Action initStaticMethodHandle(MethodHandleMember mhMember, ClassTypeVarInst lookupClass, String methodName, Type mhType, boolean isStatic) {
        String superClass = this.method.getSuperClass();
        boolean invokeSpecial = superClass != null & !isStatic;
        MethodInvokeAction findXXX;
        Action argsType = Actions.asArray(TypeUtil.CLASS_TYPE, Arrays.stream(mhType.getArgumentTypes()).map(this::loadClass).toArray(Action[]::new));
        Action returnType = mhType.getReturnType() == Type.VOID_TYPE || TypeUtil.isPrimitiveType(mhType.getReturnType())
                ? LdcLoadAction.of(mhType.getReturnType()) : this.loadClass(mhType.getReturnType());
        if (invokeSpecial) {
            findXXX = new MethodInvokeAction(MethodDescriptor.LOOKUP_FINDSPECIAL).setArgs(
                    LdcLoadAction.of(TypeUtil.getType(superClass)),
                    LdcLoadAction.of(methodName),
                    new MethodInvokeAction(MethodDescriptor.METHOD_TYPE).setArgs(returnType, argsType),
                    lookupClass
            );
        } else {
            findXXX = new MethodInvokeAction(isStatic ? MethodDescriptor.LOOKUP_FINDSTATIC : MethodDescriptor.LOOKUP_FINDVIRTUAL).setArgs(
                    superClass != null ? LdcLoadAction.of(TypeUtil.getType(superClass)) : lookupClass,
                    LdcLoadAction.of(methodName),
                    new MethodInvokeAction(MethodDescriptor.METHOD_TYPE).setArgs(returnType, argsType)
            );
        }
        return mhMember.store(findXXX.setInstance(lookupClass.getLookup()));
    }
}
