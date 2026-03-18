package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.define.md.AbsMethodMetadata;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.RuntimeUtil;
import io.github.hhy50.linker.util.AnnotationUtils;
import io.github.hhy50.linker.util.StringUtil;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Objects;

import static io.github.hhy50.linker.generate.bytecode.action.Condition.*;

/**
 * The type Abstract decorator.
 */
public abstract class AbstractDecorator extends MethodHandle {

    /**
     * The Method define.
     */
    protected final AbsMethodMetadata metadata;
    /**
     * The Auto link result.
     */
    protected boolean autolink;

    private final boolean isConstructor;

    /**
     * Instantiates a new Abstract decorator.
     *
     * @param metadata the method
     */
    protected AbstractDecorator(AbsMethodMetadata metadata) {
        Objects.requireNonNull(metadata);

        this.metadata = metadata;
        this.autolink = metadata.isAutolink();
        this.isConstructor = metadata.isConstructor();
    }

    /**
     * Typecast args.
     *
     * @param args           the args
     * @param expectArgsType the args type
     * @return the var inst [ ]
     */
    protected VarInst[] typecastArgs(VarInst[] args, Type[] expectArgsType) {
        Class<?>[] parameterTypes = metadata.getReflect().getParameterTypes();
        // 校验入参类型
        VarInst[] realArgs = new VarInst[args.length];
        for (int i = 0; i < args.length; i++) {
            VarInst arg = args[i];
            Type expectType = expectArgsType[i];
            Class<?> actualType = parameterTypes[i];

            String bindClass = AnnotationUtils.getBind(actualType);
            if (StringUtil.isNotEmpty(bindClass)) {
                arg = VarInst.wrap(arg.getTarget(), ObjectVar.TYPE);
            } else if ((this.autolink || AnnotationUtils.isAutolink(actualType)) && !actualType.isPrimitive()) {
                arg = VarInst.wrap(arg.tryGetTarget(), ObjectVar.TYPE);
            }

            VarInst newArg = typeCast(arg, expectType);
            realArgs[i] = newArg;
        }
        return realArgs;
    }

    /**
     * Typecast result var inst.
     *
     * @param retChain the var inst
     * @return the var inst
     */
    protected ChainAction<VarInst> typecastResult(ChainAction<VarInst> retChain) {
        return retChain.mapBody( (body, varInst) -> {
            Method method = metadata.getReflect();
            Class<?> returnClassType = method.getReturnType();
            Type retType = Type.getType(returnClassType);
            boolean linkresult = this.autolink || AnnotationUtils.isAutolink(returnClassType);
            if (returnClassType == Object.class && !TypeUtil.isPrimitiveType(varInst.getType())) {
                return varInst;
            }
            if (isConstructor) {
                return new CreateLinkerAction(retType, varInst);
            }
            if (retType == Type.VOID_TYPE) {
                return VarInst.wrap(varInst, Type.VOID_TYPE);
            }

            varInst = Actions.newLocalVar(varInst);
            String bindClass = AnnotationUtils.getBind(returnClassType);
            {
                Type expectType = Type.getType(returnClassType);
                if (StringUtil.isNotEmpty(bindClass)) {
                    body.append(checkType(varInst, TypeUtil.getType(bindClass)));
                    expectType = ObjectVar.TYPE;
                } else if (!returnClassType.isPrimitive() && linkresult) {
                    expectType = ObjectVar.TYPE;
                }
                varInst = typeCast(varInst, expectType);
            }

            if (Collection.class.isAssignableFrom(returnClassType) && method.getGenericReturnType() instanceof ParameterizedType) {
                java.lang.reflect.Type actualType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                Class genericType = actualType instanceof Class ? (Class) actualType : null;
                if (genericType != null && genericType.isInterface() && (AnnotationUtils.getBind(genericType) != null || linkresult))
                    return new CreateLinkerCollectAction(Type.getType(genericType), varInst);
            } else if (returnClassType.isInterface() && (StringUtil.isNotEmpty(bindClass) || linkresult)) {
                body.append(new ConditionJumpAction(
                        any(isNull(varInst), instanceOf(varInst, retType)),
                        varInst.thenReturn(), null));
                return new CreateLinkerAction(retType, varInst);
            } else if (!varInst.getType().equals(retType)) {
                return new TypeCastAction(varInst, retType);
            }
            return varInst;
        });
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
}
