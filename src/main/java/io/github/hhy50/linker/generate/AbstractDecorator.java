package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.define.AbsMethodDefine;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.type.AutoBox;
import io.github.hhy50.linker.generate.type.ContainerCast;
import io.github.hhy50.linker.generate.type.TypeCast;
import io.github.hhy50.linker.runtime.RuntimeUtil;
import io.github.hhy50.linker.util.AnnotationUtils;
import io.github.hhy50.linker.util.StringUtil;
import io.github.hhy50.linker.util.TypeUtil;
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
     * @param args       the args
     * @param expectArgsType the args type
     */
    protected ChainAction<VarInst[]> typecastArgs(VarInst[] args, Type[] expectArgsType) {
        Class<?>[] parameterTypes = absMethodDefine.method.getParameterTypes();
        return ChainAction.of((body) -> {
            // 校验入参类型
            VarInst[] realArgs = new VarInst[args.length];
            for (int i = 0; i < args.length; i++) {
                VarInst arg = args[i];
                Type expectType = expectArgsType[i];
                Class<?> actualType = parameterTypes[i];

                String bindClass = AnnotationUtils.getBind(actualType);
                if (StringUtil.isNotEmpty(bindClass)) {
                    arg = Actions.newLocalVar(ObjectVar.TYPE, arg.getTarget());
                } else if (this.autolink && !actualType.isPrimitive()) {
                    arg = Actions.newLocalVar(ObjectVar.TYPE, arg.tryGetTarget());
                }

                VarInst newArg = typeCast(arg, expectType);
                realArgs[i] = newArg;
            }
            return realArgs;
        });


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
        if (returnClassType == Object.class && !TypeUtil.isPrimitiveType(varInst.getType())) {
            return varInst;
        }

        String bindClass = AnnotationUtils.getBind(returnClassType);
        {
            Type expectType = Type.getType(returnClassType);
            if (StringUtil.isNotEmpty(bindClass)) {
                methodBody.append(checkType(varInst, TypeUtil.getType(bindClass)));
                expectType = ObjectVar.TYPE;
            } else if (!returnClassType.isPrimitive() && this.autolink) {
                expectType = ObjectVar.TYPE;
            }
            varInst = typeCast(varInst, expectType);
        }

        Type retType = Type.getType(returnClassType);
        if (Collection.class.isAssignableFrom(returnClassType) && method.getGenericReturnType() instanceof ParameterizedType) {
            java.lang.reflect.Type actualType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
            Class genericType = actualType instanceof Class ? (Class) actualType : null;
            if (genericType != null && genericType.isInterface() && (AnnotationUtils.getBind(genericType) != null || autolink))
                return Actions.newLocalVar(retType, varInst.getName(), new CreateLinkerCollectAction(Type.getType(genericType), varInst));
        } else if (returnClassType.isInterface() && (StringUtil.isNotEmpty(bindClass) || autolink)) {
            methodBody.append(new ConditionJumpAction(
                    any(isNull(varInst), instanceOf(varInst, retType)),
                    varInst.thenReturn(), null));
            return Actions.newLocalVar(varInst.getName(), new TypeCastAction(new CreateLinkerAction(retType, varInst), retType));
        } else if (!varInst.getType().equals(retType)) {
            return Actions.newLocalVar(varInst.getName(), new TypeCastAction(varInst, retType));
        }
        return varInst;
    }

    private Action checkType(VarInst varInst, Type expectType) {
        return new ConditionJumpAction(
                Condition.must(Condition.notNull(varInst),
                        Condition.ifFalse(new MethodInvokeAction(RuntimeUtil.TYPE_MATCH)
                                .setArgs(varInst.getThisClass(), LdcLoadAction.of(expectType.getClassName())))
                ),
                Actions.throwTypeCastException(varInst.getName(), expectType),
                null
        );
    }

    /**
     * Type cast var inst.
     *
     * @param varInst    the var inst
     * @param expectType 预期的类型
     * @return var inst
     */
    protected VarInst typeCast(VarInst varInst, Type expectType) {
        if (varInst.getType().equals(expectType)) {
            return varInst;
        }
        if (expectType.equals(ObjectVar.TYPE) && !TypeUtil.isPrimitiveType(varInst.getType())) {
            return varInst;
        }
        List<TypeCast> types = Arrays.asList(new AutoBox(), new ContainerCast());
        for (TypeCast type : types) {
            varInst = type.cast(varInst, expectType);
        }
        if (!varInst.getType().equals(expectType)) {
            varInst = Actions.newLocalVar(new TypeCastAction(varInst, expectType));
        }
        return varInst;
    }
}
