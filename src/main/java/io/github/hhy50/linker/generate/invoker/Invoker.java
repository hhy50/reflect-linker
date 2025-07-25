package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.SmartMethodDescriptor;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.define.method.RuntimeMethodRef;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.Runtime;
import io.github.hhy50.linker.util.TypeUtils;
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
     * @param method    the method
     * @param mType     the m type
     */
    public Invoker(T method, Type mType) {
        this.method = method;
        this.descriptor = new SmartMethodDescriptor("invoke_"+method.getUniqueName(), mType);
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        MethodInvokeAction invoker = new MethodInvokeAction(descriptor)
                .setInstance(LoadAction.LOAD0)
                .setArgs(methodBody.getArgs());

        Type rType = descriptor.getReturnType();
        if (rType.getSort() == Type.VOID) {
            methodBody.append(invoker);
            return null;
        } else {
            return methodBody.newLocalVar(rType, null, invoker);
        }
    }

    @Override
    protected void initRuntimeMethodHandle(MethodBody methodBody, ClassTypeMember lookupClass, MethodHandleMember mhMember, VarInst objVar) {
        RuntimeMethodRef runtime = (RuntimeMethodRef) method;
        Class<Action> __ = Action.class;
        Action superClassLoad = Optional.ofNullable(method.getSuperClass())
                .map(LdcLoadAction::of)
                .map(__::cast)
                .orElseGet(Actions::loadNull);
        MethodInvokeAction fineMethod = new MethodInvokeAction(Runtime.FIND_METHOD)
                .setArgs(lookupClass.getLookup(methodBody), lookupClass,
                        LdcLoadAction.of(method.getName()),
                        superClassLoad,
                        Actions.asArray(TypeUtils.STRING_TYPE, Arrays.stream(runtime.getArgsType())
                                .map(Type::getClassName).map(LdcLoadAction::of).toArray(Action[]::new))
                );
        mhMember.store(methodBody, fineMethod);
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
