package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.Runtime;
import io.github.hhy50.linker.util.ClassUtil;
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
     * The Method type.
     */
    protected final Type methodType;
    /**
     * The Method holder.
     */
    protected final MethodDescriptor methodDescriptor;

    /**
     * Instantiates a new Invoker.
     *
     * @param implClass the impl class
     * @param method    the method
     * @param mType     the m type
     */
    public Invoker(String implClass, T method, Type mType) {
        this.method = method;
        this.methodType = mType;
        this.methodDescriptor = MethodDescriptor.of(ClassUtil.className2path(implClass),
                "invoke_"+method.getFullName(),
                this.methodType.getDescriptor());
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        MethodInvokeAction invoker = new MethodInvokeAction(methodDescriptor)
                .setInstance(LoadAction.LOAD0)
                .setArgs(methodBody.getArgs());

        Type rType = methodType.getReturnType();
        if (rType.getSort() == Type.VOID) {
            methodBody.append(invoker);
            return null;
        } else {
            return methodBody.newLocalVar(methodType.getReturnType(), null, invoker);
        }
    }

    @Override
    protected void mhReassign(MethodBody methodBody, ClassTypeMember lookupClass, MethodHandleMember mhMember, VarInst objVar) {
        Class<Action> __ = Action.class;
        Action superClassLoad = Optional.ofNullable(method.getSuperClass())
                .map(LdcLoadAction::of)
                .map(__::cast)
                .orElseGet(Actions::loadNull);
        MethodInvokeAction fineMethod = new MethodInvokeAction(Runtime.FIND_METHOD)
                .setArgs(lookupClass.getLookup(methodBody), lookupClass,
                        LdcLoadAction.of(method.getName()),
                        superClassLoad,
                        Actions.asArray(Type.getType(String.class), Arrays.stream(method.getArgsType())
                                .map(Type::getClassName).map(LdcLoadAction::of).toArray(Action[]::new))
                        // Object[] args
//                        Actions.asArray(Type.getType(Object.class),
//                                IntStream.range(0, methodBody.getArgs().length)
//                                        .mapToObj(Args::of).map(BoxAction::new).toArray(Action[]::new))
                );
        mhMember.store(methodBody, fineMethod);
    }
}
