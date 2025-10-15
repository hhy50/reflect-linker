package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
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
     * 方法名字
     */
    protected String methodName;

    /**
     * 目前 methodType == mh的执行时签名, 即方法的定义的类型完全等于 methodhandle 的类型
     */
    protected final Type methodType;

    /**
     * 调用的父类
     */
    protected String superClass;

    /**
     * Instantiates a new Invoker.
     *
     * @param methodName the methodName
     * @param mType  the m type
     */
    public Invoker(String methodName, Type mType) {
        this.methodName = methodName;
        this.methodType = mType;
    }

    @Override
    protected Action initRuntimeMethodHandle(MethodHandleMember mhMember, ClassTypeMember lookupClass, Type mhType) {
        Class<Action> __ = Action.class;
        Action superClassLoad = Optional.ofNullable(this.superClass)
                .map(LdcLoadAction::of)
                .map(__::cast)
                .orElseGet(Actions::loadNull);
        MethodInvokeAction findMethod = new MethodInvokeAction(Runtime.FIND_METHOD)
                .setArgs(lookupClass.getLookup(), lookupClass,
                        LdcLoadAction.of(this.methodName),
                        superClassLoad,
                        Actions.asArray(TypeUtil.STRING_TYPE, Arrays.stream(mhType.getArgumentTypes())
                                .map(Type::getClassName).map(LdcLoadAction::of).toArray(Action[]::new))
                );
        return mhMember.store(findMethod);
    }

    @Override
    protected Action initStaticMethodHandle(MethodHandleMember mhMember, ClassTypeVarInst lookupClass, boolean isStatic) {
        boolean invokeSpecial = this.superClass != null & !isStatic;
        MethodInvokeAction findXXX;
        Action argsType = Actions.asArray(TypeUtil.CLASS_TYPE, Arrays.stream(mhType.getArgumentTypes()).map(this::loadClass).toArray(Action[]::new));
        Action returnType = mhType.getReturnType() == Type.VOID_TYPE || TypeUtil.isPrimitiveType(mhType.getReturnType())
                ? LdcLoadAction.of(mhType.getReturnType()) : this.loadClass(mhType.getReturnType());
        if (invokeSpecial) {
            findXXX = new MethodInvokeAction(MethodDescriptor.LOOKUP_FINDSPECIAL).setArgs(
                    LdcLoadAction.of(TypeUtil.getType(this.superClass)),
                    LdcLoadAction.of(methodName),
                    new MethodInvokeAction(MethodDescriptor.METHOD_TYPE).setArgs(returnType, argsType),
                    lookupClass
            );
        } else {
            findXXX = new MethodInvokeAction(isStatic ? MethodDescriptor.LOOKUP_FINDSTATIC : MethodDescriptor.LOOKUP_FINDVIRTUAL).setArgs(
                    this.superClass != null ? LdcLoadAction.of(TypeUtil.getType(this.superClass)) : lookupClass,
                    LdcLoadAction.of(methodName),
                    new MethodInvokeAction(MethodDescriptor.METHOD_TYPE).setArgs(returnType, argsType)
            );
        }
        return mhMember.store(findXXX.setInstance(lookupClass.getLookup()));
    }
}
