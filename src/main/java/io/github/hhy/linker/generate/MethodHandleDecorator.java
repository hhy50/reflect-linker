package io.github.hhy.linker.generate;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.exceptions.TypeNotMatchException;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.action.ConditionJumpAction;
import io.github.hhy.linker.generate.bytecode.action.TypeCastAction;
import io.github.hhy.linker.generate.bytecode.action.UnwrapTypeAction;
import io.github.hhy.linker.generate.bytecode.action.WrapTypeAction;
import io.github.hhy.linker.generate.bytecode.vars.LocalVarInst;
import io.github.hhy.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Type;

import static io.github.hhy.linker.generate.bytecode.action.Action.throwTypeCastException;
import static io.github.hhy.linker.generate.bytecode.action.Condition.instanceOf;
import static org.objectweb.asm.Opcodes.ILOAD;

public abstract class MethodHandleDecorator extends MethodHandle {

    @Override
    protected void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, VarInst objVar) {
        throw new RuntimeException("Decorator not impl mhReassign() method");
    }

    protected void typecastArgs(MethodBody methodBody, VarInst[] args, Type[] realTypes) {
        // 校验入参类型
        for (int i = 0; i < args.length; i++) {
            Type argType = args[i].getType();
            Type realType = realTypes[i];

            if (!argType.equals(realType)) {
                LocalVarInst varInst = null;
                if (AsmUtil.isPrimitiveType(argType)) {
                    varInst = methodBody.newLocalVar(ObjectVar.TYPE, null, new WrapTypeAction(args[i]));
                }
                varInst = methodBody.newLocalVar(realTypes[i], null, new TypeCastAction(varInst == null ? args[i] : varInst, realType));
                methodBody.getArgs()[0] = varInst;
            }
        }
    }

    /**
     * 基本数据类型 -> 对象类型 = 装箱
     * 对象类型 -> 基本数据类型 = 拆箱
     * 对象类型 -> 对象类型 = 强转
     *
     * @param methodBody
     * @param result
     * @param expectTypeClass 预期的类型
     */
    protected void typecastResult(MethodBody methodBody, VarInst result, Class<?> expectTypeClass) {
        Type expectType = Type.getType(expectTypeClass);
        boolean r1 = AsmUtil.isPrimitiveType(expectType);
        boolean r2 = AsmUtil.isPrimitiveType(result.getType());
        boolean castSuccess = false;

        if (r1 && r2) {
            if (result.getType().getOpcode(ILOAD) == expectType.getOpcode(ILOAD)) {
                result.load(methodBody);
                castSuccess = true;
            }
        } else {
            if (r1 && AsmUtil.isWrapType(result.getType())) {
                methodBody.append(() -> new UnwrapTypeAction(result));
                castSuccess = true;
            } else if (r2 && ClassUtil.isWrapClass(expectTypeClass)) {
                methodBody.append(() -> new WrapTypeAction(result)
                        .onAfter(new TypeCastAction(null, expectType)));
                castSuccess = true;
            }
        }
        if (castSuccess) {
            AsmUtil.areturn(methodBody.getWriter(), expectType);
            return;
        }

        if (r1 == r2) {
            methodBody.append(() -> new ConditionJumpAction(
                    instanceOf(result, expectType),
                    (body) -> {
                        body.append(() -> new TypeCastAction(result, expectType).onAfter((__) -> AsmUtil.areturn(body.getWriter(), expectType)));
                    },
                    throwTypeCastException(result.getType(), expectType))
            );
        } else {
            throw new TypeNotMatchException(result.getType(), expectType);
        }
    }
}
