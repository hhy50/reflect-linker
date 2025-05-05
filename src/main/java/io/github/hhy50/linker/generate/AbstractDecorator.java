package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.AbsMethodDefine;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.type.AutoBox;
import io.github.hhy50.linker.generate.type.ContainerCast;
import io.github.hhy50.linker.generate.type.TypeCast;
import io.github.hhy50.linker.runtime.RuntimeUtil;
import io.github.hhy50.linker.util.AnnotationUtils;
import io.github.hhy50.linker.util.StringUtil;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static io.github.hhy50.linker.generate.bytecode.action.Condition.*;

/**
 * The type Abstract decorator.
 */
public abstract class AbstractDecorator extends MethodHandle {

    /**
     * The Method define.
     */
    protected final AbsMethodDefine absMethodDefine;

    /**
     * The Auto link result.
     */
    protected boolean autolink;

    /**
     * Instantiates a new Abstract decorator.
     *
     * @param absMethodDefine the method define
     */
    protected AbstractDecorator(AbsMethodDefine absMethodDefine) {
        Objects.requireNonNull(absMethodDefine);

        this.absMethodDefine = absMethodDefine;
        this.autolink = AnnotationUtils.isAutolink(absMethodDefine.method);
    }

    /**
     * Typecast args.
     *
     * @param methodBody the method body
     * @param args       the args
     * @param argsType   the args type
     */
    protected void typecastArgs(MethodBody methodBody, VarInst[] args, Type[] argsType) {
        Class<?>[] parameterTypes = absMethodDefine.method.getParameterTypes();

        // 校验入参类型
        VarInst[] realArgs = methodBody.getArgs();
        for (int i = 0; i < args.length; i++) {
            VarInst arg = args[i];
            Type expectType = argsType[i];
            Class<?> actualType = parameterTypes[i];

            String bindClass = AnnotationUtils.getBind(actualType);
            if (StringUtil.isNotEmpty(bindClass)) {
                arg = methodBody.newLocalVar(ObjectVar.TYPE, null, arg.getTarget());
            } else if (this.autolink && !actualType.isPrimitive()) {
                arg = methodBody.newLocalVar(ObjectVar.TYPE, null, arg.tryGetTarget());
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
        Method method = absMethodDefine.method;
        Class<?> returnClassType = method.getReturnType();
        if (returnClassType == Object.class && !AsmUtil.isPrimitiveType(varInst.getType())) {
            return varInst;
        }
        String bindClass = AnnotationUtils.getBind(returnClassType);
        {
            Type expectType = Type.getType(returnClassType);
            if (StringUtil.isNotEmpty(bindClass)) {
                checkType(methodBody, varInst, AsmUtil.getType(bindClass));
                expectType = ObjectVar.TYPE;
            } else if (this.autolink) {
                expectType = ObjectVar.TYPE;
            }
            varInst = typeCast(methodBody, varInst, expectType);
        }

        Type retType = Type.getType(returnClassType);
        if ((StringUtil.isNotEmpty(bindClass) || (autolink && returnClassType.isInterface()))) {
            methodBody.append(new ConditionJumpAction(
                    any(isNull(varInst), instanceOf(varInst, retType)),
                    varInst.thenReturn(), null));

            return methodBody.newLocalVar(varInst.getName(), new TypeCastAction(new CreateLinkerAction(retType, varInst), retType));
        } else if (!varInst.getType().equals(retType)) {
            return methodBody.newLocalVar(varInst.getName(), new TypeCastAction(varInst, retType));
        } else if (Collection.class.isAssignableFrom(returnClassType) && method.getGenericReturnType() instanceof ParameterizedType) {
            java.lang.reflect.Type actualType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
            Class genericType = actualType instanceof Class ? (Class) actualType : null;
            if (genericType != null && AnnotationUtils.getBind(genericType) != null)
                return methodBody.newLocalVar(retType, varInst.getName(), new CreateLinkerCollectAction(Type.getType(genericType), varInst));
        }
        return varInst;
    }

    private void checkType(MethodBody methodBody, VarInst varInst, Type expectType) {
        methodBody.append(new ConditionJumpAction(
                Condition.must(Condition.notNull(varInst),
                        Condition.ifFalse(new MethodInvokeAction(RuntimeUtil.TYPE_MATCH)
                                .setArgs(varInst.getThisClass(), LdcLoadAction.of(expectType.getClassName())))
                ),
                Actions.throwTypeCastException(varInst.getName(), expectType),
                null
        ));
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
            varInst = methodBody.newLocalVar(new TypeCastAction(varInst, expectType));
        }
        return varInst;
    }

    @Override
    protected void initRuntimeMethodHandle(MethodBody methodBody, ClassTypeMember classType, MethodHandleMember mhMember, VarInst objVar) {
        throw new RuntimeException("Decorator not impl mhReassign() method");
    }
}
