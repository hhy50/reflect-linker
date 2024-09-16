package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.exceptions.TypeNotMatchException;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.RuntimeUtil;
import io.github.hhy50.linker.util.AnnotationUtils;
import io.github.hhy50.linker.util.StringUtil;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.ILOAD;

/**
 * <p>Abstract AbstractDecorator class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public abstract class AbstractDecorator extends MethodHandle {


    /**
     * <p>typecastArgs.</p>
     *
     * @param methodBody     a {@link io.github.hhy50.linker.generate.MethodBody} object.
     * @param args           an array of {@link io.github.hhy50.linker.generate.bytecode.vars.VarInst} objects.
     * @param parameterTypes an array of {@link java.lang.Class} objects.
     * @param expectTypes    an array of {@link org.objectweb.asm.Type} objects.
     */
    protected void typecastArgs(MethodBody methodBody, VarInst[] args, Class<?>[] parameterTypes, Type[] expectTypes) {
        // 校验入参类型
        for (int i = 0; i < args.length; i++) {
            VarInst arg = args[i];
            Type expectType = expectTypes[i];

            String bindClass = AnnotationUtils.getBind(parameterTypes[i]);
            if (StringUtil.isNotEmpty(bindClass)) {
                Type type = AsmUtil.getType(bindClass);
                arg = methodBody.newLocalVar(type, arg.getTarget(type));
            }

            VarInst newArg = typeCheck(methodBody, arg, expectType);
            methodBody.getArgs()[i] = newArg;
        }
    }

    /**
     * <p>typecastResult.</p>
     *
     * @param methodBody      a {@link io.github.hhy50.linker.generate.MethodBody} object.
     * @param varInst         a {@link io.github.hhy50.linker.generate.bytecode.vars.VarInst} object.
     * @param resultTypeClass a {@link java.lang.Class} object.
     * @return a {@link io.github.hhy50.linker.generate.bytecode.vars.VarInst} object.
     */
    protected VarInst typecastResult(MethodBody methodBody, VarInst varInst, Class<?> resultTypeClass) {
        Type expectType = Type.getType(resultTypeClass);
        String bindClass = AnnotationUtils.getBind(resultTypeClass);
        varInst = typeCheck(methodBody, varInst, StringUtil.isNotEmpty(bindClass)
                ? AsmUtil.getType(bindClass) : expectType);
        if (StringUtil.isNotEmpty(bindClass)) {
            varInst = methodBody.newLocalVar(expectType, new CreateLinkerAction(expectType, varInst));
            return methodBody.newLocalVar(expectType, varInst.getName(), new TypeCastAction(varInst, expectType));
        } else if (!varInst.getType().equals(expectType)) {
            return methodBody.newLocalVar(expectType, varInst.getName(), new TypeCastAction(varInst, expectType));
        }
        return varInst;
    }


    /**
     * <p>基本数据类型 to 对象类型 = 装箱</p>
     * <p>对象类型 to 基本数据类型 = 拆箱</p>
     * <p>对象类型 to 对象类型 = 强转</p>
     *
     * @param methodBody a {@link io.github.hhy50.linker.generate.MethodBody} object.
     * @param varInst    a {@link io.github.hhy50.linker.generate.bytecode.vars.VarInst} object.
     * @param expectType 预期的类型
     * @return a {@link io.github.hhy50.linker.generate.bytecode.vars.VarInst} object.
     */
    protected VarInst typeCheck(MethodBody methodBody, VarInst varInst, Type expectType) {
        if (varInst.getType().equals(expectType)) {
            return varInst;
        }

        boolean r1 = AsmUtil.isPrimitiveType(expectType);
        boolean r2 = AsmUtil.isPrimitiveType(varInst.getType());
        if (r1 && r2) {
            if (varInst.getType().getOpcode(ILOAD) == expectType.getOpcode(ILOAD)) {
                return varInst;
            }
            throw new TypeNotMatchException(varInst.getType(), expectType);
        }

        // 拆装箱
        if (r1 && (AsmUtil.isWrapType(varInst.getType()) || varInst.getType().getClassName().equals(Object.class.getName()))) {
            return methodBody.newLocalVar(expectType, new UnwrapTypeAction(varInst, expectType));
        } else if (r2 && (AsmUtil.isWrapType(expectType) || expectType.getClassName().equals(Object.class.getName()))) {
            return methodBody.newLocalVar(expectType, new WrapTypeAction(varInst).onAfter(new TypeCastAction(Action.stackTop(), expectType)));
        }

        if (r1 == r2) {
            if (expectType.equals(ObjectVar.TYPE)) {
                return varInst;
            }
            methodBody.append(() -> new ConditionJumpAction(
                    Condition.must(Condition.notNull(varInst),
                            Condition.ifFalse(new MethodInvokeAction(RuntimeUtil.TYPE_MATCH)
                                    .setArgs(varInst.getThisClass(), LdcLoadAction.of(expectType.getClassName())))
                    ),
                    Action.throwTypeCastException(varInst.getName(), expectType),
                    null
            ));
            return varInst;
        } else {
            throw new TypeNotMatchException(varInst.getType(), expectType);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void mhReassign(MethodBody methodBody, ClassTypeMember classType, MethodHandleMember mhMember, VarInst objVar) {
        throw new RuntimeException("Decorator not impl mhReassign() method");
    }
}
