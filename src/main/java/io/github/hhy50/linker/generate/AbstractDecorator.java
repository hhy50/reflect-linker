package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.type.AutoBox;
import io.github.hhy50.linker.generate.type.ContainerCast;
import io.github.hhy50.linker.generate.type.TypeCast;
import io.github.hhy50.linker.runtime.RuntimeUtil;
import io.github.hhy50.linker.util.AnnotationUtils;
import io.github.hhy50.linker.util.StringUtil;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.List;

/**
 * The type Abstract decorator.
 */
public abstract class AbstractDecorator extends MethodHandle {

    /**
     * The Auto link result.
     */
    protected boolean autoLinkResult;

    /**
     * Typecast args.
     *
     * @param methodBody     the method body
     * @param args           the args
     * @param parameterTypes the parameter types
     * @param expectTypes    the expect types
     */
    protected void typecastArgs(MethodBody methodBody, VarInst[] args, Class<?>[] parameterTypes, Type[] expectTypes) {
        // 校验入参类型
        VarInst[] realArgs = methodBody.getArgs();
        for (int i = 0; i < args.length; i++) {
            VarInst arg = args[i];
            Type expectType = expectTypes[i];

            String bindClass = AnnotationUtils.getBind(parameterTypes[i]);
            if (StringUtil.isNotEmpty(bindClass)) {
                Type type = AsmUtil.getType(bindClass);
                arg = methodBody.newLocalVar(type, arg.getTarget(type));
            }

            VarInst newArg = typeCast(methodBody, arg, expectType);
            realArgs[i] = newArg;
        }
    }

    /**
     * Typecast result var inst.
     *
     * @param methodBody      the method body
     * @param varInst         the var inst
     * @param resultTypeClass the result type class
     * @return the var inst
     */
    protected VarInst typecastResult(MethodBody methodBody, VarInst varInst, Class<?> resultTypeClass) {
        if (resultTypeClass == Object.class && !AsmUtil.isPrimitiveType(varInst.getType())) {
            return varInst;
        }
        Type expectType = Type.getType(resultTypeClass);
        String bindClass = AnnotationUtils.getBind(resultTypeClass);
        if (!autoLinkResult) {
            varInst = typeCast(methodBody, varInst, StringUtil.isNotEmpty(bindClass)
                    ? AsmUtil.getType(bindClass) : expectType);
        }
        if (StringUtil.isNotEmpty(bindClass) || autoLinkResult) {
            varInst = methodBody.newLocalVar(expectType, new CreateLinkerAction(expectType, varInst));
            return methodBody.newLocalVar(expectType, varInst.getName(), new TypeCastAction(varInst, expectType));
        } else if (!varInst.getType().equals(expectType)) {
            return methodBody.newLocalVar(expectType, varInst.getName(), new TypeCastAction(varInst, expectType));
        }
        return varInst;
    }

    /**
     *
     * @param methodBody
     * @param varInst
     * @param expectType 预期的类型
     * @return
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
            methodBody.append(new ConditionJumpAction(
                    Condition.must(Condition.notNull(varInst),
                            Condition.ifFalse(new MethodInvokeAction(RuntimeUtil.TYPE_MATCH)
                                    .setArgs(varInst.getThisClass(), LdcLoadAction.of(expectType.getClassName())))
                    ),
                    Actions.throwTypeCastException(varInst.getName(), expectType),
                    null
            ));
            varInst = methodBody.newLocalVar(expectType, new TypeCastAction(varInst, expectType));
        }
        return varInst;
    }

    @Override
    protected void mhReassign(MethodBody methodBody, ClassTypeMember classType, MethodHandleMember mhMember, VarInst objVar) {
        throw new RuntimeException("Decorator not impl mhReassign() method");
    }
}
