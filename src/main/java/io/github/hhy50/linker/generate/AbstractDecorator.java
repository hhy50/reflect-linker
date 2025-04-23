package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.MethodDefine;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.type.AutoBox;
import io.github.hhy50.linker.generate.type.ContainerCast;
import io.github.hhy50.linker.generate.type.TypeCast;
import io.github.hhy50.linker.util.AnnotationUtils;
import io.github.hhy50.linker.util.StringUtil;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * The type Abstract decorator.
 */
public abstract class AbstractDecorator extends MethodHandle {

    /**
     * The Method define.
     */
    protected final MethodDefine methodDefine;

    /**
     * The Auto link result.
     */
    protected boolean autolink;

    /**
     * Instantiates a new Abstract decorator.
     *
     * @param methodDefine the method define
     */
    protected AbstractDecorator(MethodDefine methodDefine) {
        Objects.requireNonNull(methodDefine);

        this.methodDefine = methodDefine;
        this.autolink = AnnotationUtils.isAutolink(methodDefine.method);
    }

    /**
     * Typecast args.
     *
     * @param methodBody     the method body
     * @param args           the args
     * @param expectTypes    the expect types
     */
    protected void typecastArgs(MethodBody methodBody, VarInst[] args, Type[] expectTypes) {
        Class<?>[] parameterTypes = methodDefine.method.getParameterTypes();
        // 校验入参类型
        VarInst[] realArgs = methodBody.getArgs();
        for (int i = 0; i < args.length; i++) {
            VarInst arg = args[i];
            Type expectType = expectTypes[i];

            String bindClass = AnnotationUtils.getBind(parameterTypes[i]);
            if (StringUtil.isNotEmpty(bindClass)) {
                Type type = AsmUtil.getType(bindClass);
                arg = methodBody.newLocalVar(type, arg.getTarget(type));
            } else if (this.autolink) {
                arg = methodBody.newLocalVar(ObjectVar.TYPE, arg.tryGetTarget());
            }

            VarInst newArg = typeCast(methodBody, arg, expectType);
            realArgs[i] = newArg;
        }
    }

    /**
     * Typecast result var inst.
     *
     * @param methodBody the method body
     * @param varInst    the var inst
     * @return the var inst
     */
    protected VarInst typecastResult(MethodBody methodBody, VarInst varInst) {
        Method method = methodDefine.method;
        Class<?> returnType = method.getReturnType();
        if (returnType == Object.class && !AsmUtil.isPrimitiveType(varInst.getType())) {
            return varInst;
        }
        Type expectType = Type.getType(returnType);
        String bindClass = AnnotationUtils.getBind(returnType);
        if (!autolink) {
            varInst = typeCast(methodBody, varInst, StringUtil.isNotEmpty(bindClass)
                    ? AsmUtil.getType(bindClass) : expectType);
        }
        if (StringUtil.isNotEmpty(bindClass) || autolink) {
            return methodBody.newLocalVar(expectType, varInst.getName(), new TypeCastAction(new CreateLinkerAction(expectType, varInst), expectType));
        } else if (!varInst.getType().equals(expectType)) {
            return methodBody.newLocalVar(expectType, varInst.getName(), new TypeCastAction(varInst, expectType));
        } else if (Collection.class.isAssignableFrom(returnType) && method.getGenericReturnType() instanceof ParameterizedType) {
            java.lang.reflect.Type actualType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
            Class genericType = actualType instanceof Class ? (Class) actualType : null;
            if (genericType != null && AnnotationUtils.getBind(genericType) != null)
                return methodBody.newLocalVar(expectType, varInst.getName(), new CreateLinkerCollectAction(Type.getType(genericType), varInst));
        }
        return varInst;
    }

    /**
     * Type cast var inst.
     *
     * @param methodBody the method body
     * @param varInst    the var inst
     * @param expectType 预期的类型
     * @return var inst
     */
    protected VarInst typeCast(MethodBody methodBody, VarInst varInst, Type expectType) {
        if (varInst.getType().equals(expectType)) {
            return varInst;
        }
        List<TypeCast> types = Arrays.asList(new AutoBox(), new ContainerCast());
        for (TypeCast type : types) {
            varInst = type.cast(methodBody, varInst, expectType);
        }

        if (!varInst.getType().equals(expectType)) {
            varInst = methodBody.newLocalVar(expectType, new TypeCastAction(varInst, expectType));
        }
        return varInst;
    }

    @Override
    protected void mhReassign(MethodBody methodBody, ClassTypeMember classType, MethodHandleMember mhMember, VarInst objVar) {
        throw new RuntimeException("Decorator not impl mhReassign() method");
    }
}
