package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.method.EarlyMethodRef;
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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

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
    protected MethodDescriptor methodDescriptor;
    /**
     * The generic.
     */
    protected boolean generic;

    /**
     * Instantiates a new Invoker.
     *
     * @param implClass the impl class
     * @param method    the method
     * @param mType     the m type
     */
    public Invoker(String implClass, T method, Type mType) {
        this.method = method;
        this.generic = isUnreachable(method);
        this.methodType = generic ? genericType(mType) : mType;
        this.methodDescriptor = MethodDescriptor.of(ClassUtil.className2path(implClass), "invoke_"+method.getFullName(), methodType.getDescriptor());
    }

    /**
     * 如果存在无法访问的类, 就擦除类型转为Object
     *
     * @param methodDefine
     * @return
     */
    private boolean isUnreachable(T methodDefine) {
        if (methodDefine instanceof EarlyMethodRef) {
            Method method = ((EarlyMethodRef) methodDefine).getMethod();
            if (!Modifier.isPublic(method.getReturnType().getModifiers())) {
                return true;
            }
            for (Class<?> parameterType : method.getParameterTypes()) {
                if (!Modifier.isPublic(parameterType.getModifiers())) {
                    return true;
                }
            }
            return false;
        }
        return true;
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
        String superClass = method.getSuperClass();
        Action superClassLoad = superClass != null ? LdcLoadAction.of(superClass) : Actions.loadNull();
        MethodInvokeAction findGetter = new MethodInvokeAction(Runtime.FIND_METHOD)
                .setArgs(lookupClass.getLookup(methodBody), lookupClass,
                        LdcLoadAction.of(method.getName()),
                        superClassLoad,
                        Actions.asArray(Type.getType(String.class), Arrays.stream(method.getArgsType())
                                .map(Type::getClassName).map(LdcLoadAction::of).toArray(Action[]::new))
                );
        mhMember.store(methodBody, findGetter);
    }
}
