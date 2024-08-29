package io.github.hhy.linker.generate;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.exceptions.TypeNotMatchException;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.action.ConditionJumpAction;
import io.github.hhy.linker.generate.bytecode.action.TypeCastAction;
import io.github.hhy.linker.generate.bytecode.action.UnwrapTypeAction;
import io.github.hhy.linker.generate.bytecode.action.WrapTypeAction;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

import static io.github.hhy.linker.generate.bytecode.action.Action.throwTypeCastException;
import static io.github.hhy.linker.generate.bytecode.action.Condition.instanceOf;
import static org.objectweb.asm.Opcodes.ILOAD;

public abstract class MethodHandleDecorator extends MethodHandle {

    @Override
    protected void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, VarInst objVar) {
        throw new RuntimeException("Decorator not impl mhReassign() method");
    }

    protected void typecastArgs(MethodBody methodBody, VarInst[] args, Type[] expectTypes) {
        // 校验入参类型
        for (int i = 0; i < args.length; i++) {
            Type expectType = expectTypes[i];

            VarInst newArg = typecast(methodBody, args[i], expectType);
            methodBody.getArgs()[i] = newArg;
        }
    }

    /**
     * 基本数据类型 -> 对象类型 = 装箱
     * 对象类型 -> 基本数据类型 = 拆箱
     * 对象类型 -> 对象类型 = 强转
     *
     * @param methodBody
     * @param varInst
     * @param expectType 预期的类型
     */
    protected VarInst typecast(MethodBody methodBody, VarInst varInst, Type expectType) {
        boolean r1 = AsmUtil.isPrimitiveType(expectType);
        boolean r2 = AsmUtil.isPrimitiveType(varInst.getType());
        if (r1 && r2) {
            if (varInst.getType().getOpcode(ILOAD) == expectType.getOpcode(ILOAD)) {
                return varInst;
            }
            throw new TypeNotMatchException(varInst.getType(), expectType);
        }

        // 拆装箱
        if (r1 && AsmUtil.isWrapType(varInst.getType())) {
            return methodBody.newLocalVar(expectType, new UnwrapTypeAction(varInst));
        } else if (r2 && (AsmUtil.isWrapType(expectType) || expectType.getClassName().equals(Object.class.getName()))) {
            return methodBody.newLocalVar(expectType, new WrapTypeAction(varInst).onAfter(new TypeCastAction(null, expectType)));
        }

        if (r1 == r2) {
            VarInst expectVar = methodBody.newLocalVar(expectType, null);
            methodBody.append(() -> new ConditionJumpAction(
                    instanceOf(varInst, expectType),
                    (body) -> expectVar.store(body, new TypeCastAction(varInst, expectType)),
                    throwTypeCastException(varInst.getType(), expectType))
            );
            return expectVar;
        } else {
            throw new TypeNotMatchException(varInst.getType(), expectType);
        }
    }
}
