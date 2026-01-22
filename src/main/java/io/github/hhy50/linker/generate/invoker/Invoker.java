package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodDescriptor;
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
     *
     */
    protected Type lookupClass;
    /**
     * 方法名字
     */
    protected String lookupName;

    /**
     * 查找 method handle时的类型
     */
    protected Type lookupType;

    /**
     * 调用的父类
     */
    protected String superClass;

    /**
     * Instantiates a new Invoker.
     *
     * @param lookupName the methodName
     * @param lookupType the lookup type
     */
    public Invoker(String lookupName, Type lookupType) {
        this.lookupName = lookupName;
        this.lookupType = lookupType;
    }

    @Override
    protected Action initRuntimeMethodHandle(MethodHandleMember mhMember, ClassTypeMember lookupClass) {
        Class<Action> __ = Action.class;
        Type[] argumentTypes = this.lookupType.getArgumentTypes();

        Action superClassLoad = Optional.ofNullable(this.superClass)
                .map(LdcLoadAction::of)
                .map(__::cast)
                .orElseGet(Actions::loadNull);
        MethodInvokeAction findMethod = new MethodInvokeAction(Runtime.FIND_METHOD)
                .setArgs(lookupClass.getLookup(), lookupClass,
                        LdcLoadAction.of(this.lookupName),
                        superClassLoad,
                        Actions.asArray(TypeUtil.STRING_TYPE, Arrays.stream(argumentTypes)
                                .map(Type::getClassName).map(LdcLoadAction::of).toArray(Action[]::new))
                );
        return mhMember.store(findMethod);
    }

    @Override
    protected Action initStaticMethodHandle(MethodHandleMember mhMember, ClassTypeVarInst lookupClass, boolean isStatic) {
        boolean invokeSpecial = this.superClass != null & !isStatic;
        Type[] argumentTypes = this.lookupType.getArgumentTypes();
        Type returnType = this.lookupType.getReturnType();

        MethodInvokeAction findXXX;
        Action mhArgsTypeLoad = Actions.asArray(TypeUtil.CLASS_TYPE, Arrays.stream(argumentTypes).map(this::loadClass).toArray(Action[]::new));
        Action mhRetTypeLoad = returnType == Type.VOID_TYPE || TypeUtil.isPrimitiveType(returnType)
                ? LdcLoadAction.of(returnType) : this.loadClass(returnType);
        if (invokeSpecial) {
            findXXX = new MethodInvokeAction(MethodDescriptor.LOOKUP_FINDSPECIAL).setArgs(
                    LdcLoadAction.of(TypeUtil.getType(this.superClass)),
                    LdcLoadAction.of(lookupName),
                    new MethodInvokeAction(MethodDescriptor.METHOD_TYPE).setArgs(mhRetTypeLoad, mhArgsTypeLoad),
                    lookupClass
            );
        } else {
            findXXX = new MethodInvokeAction(isStatic ? MethodDescriptor.LOOKUP_FINDSTATIC : MethodDescriptor.LOOKUP_FINDVIRTUAL).setArgs(
                    this.superClass != null ? LdcLoadAction.of(TypeUtil.getType(this.superClass)) : lookupClass,
                    LdcLoadAction.of(lookupName),
                    new MethodInvokeAction(MethodDescriptor.METHOD_TYPE).setArgs(mhRetTypeLoad, mhArgsTypeLoad)
            );
        }
        return mhMember.store(findXXX.setInstance(lookupClass.getLookup()));
    }
}
