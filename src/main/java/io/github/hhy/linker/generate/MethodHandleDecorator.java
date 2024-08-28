package io.github.hhy.linker.generate;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.action.TypeCastAction;
import io.github.hhy.linker.generate.bytecode.action.UnwrapTypeAction;
import io.github.hhy.linker.generate.bytecode.action.WrapTypeAction;
import io.github.hhy.linker.generate.bytecode.vars.LocalVarInst;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

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
                    varInst = methodBody.newLocalVar(argType, null, new WrapTypeAction(args[i]));
                }
                varInst = methodBody.newLocalVar(argType, null, new TypeCastAction(varInst == null ? args[i] : varInst, realType));
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
     * @param realType
     */
    protected Type typecastResult(MethodBody methodBody, VarInst result, Type realType) {
        if (result.getType().equals(realType)) {
            result.load(methodBody);
        } else if (AsmUtil.isPrimitiveType(realType)) {
            methodBody.append(() -> new UnwrapTypeAction(result, realType));
        } else if (AsmUtil.isPrimitiveType(result.getType())) {
            methodBody.append(() -> new WrapTypeAction(result));
        } else {
            methodBody.append(() -> new TypeCastAction(result, realType));
        }
        return realType;
    }
}
