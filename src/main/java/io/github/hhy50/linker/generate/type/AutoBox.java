package io.github.hhy50.linker.generate.type;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.exceptions.TypeNotMatchException;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.BoxAction;
import io.github.hhy50.linker.generate.bytecode.action.UnBoxAction;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.ILOAD;


/**
 * 自动拆装箱
 */
public class AutoBox implements TypeCast {

    @Override
    public VarInst cast(MethodBody methodBody, VarInst varInst, Type expectType) {
        boolean r1 = AsmUtil.isPrimitiveType(expectType);
        boolean r2 = AsmUtil.isPrimitiveType(varInst.getType());
        if (r1 && r2) {
            if (varInst.getType().getOpcode(ILOAD) == expectType.getOpcode(ILOAD)) {
                return varInst;
            }
            throw new TypeNotMatchException(varInst.getType(), expectType);
        } else if (r1 && (AsmUtil.isWrapType(varInst.getType()) || varInst.getType().equals(ObjectVar.TYPE))) {
            return methodBody.newLocalVar(new UnBoxAction(varInst, expectType));
        } else if (r2 && (AsmUtil.isWrapType(expectType) || expectType.equals(ObjectVar.TYPE))) {
            return methodBody.newLocalVar(new BoxAction(varInst, expectType));
        } else if (r1 || r2) {
            throw new TypeNotMatchException(varInst.getType(), expectType);
        } else {
            return varInst;
        }
    }
}
