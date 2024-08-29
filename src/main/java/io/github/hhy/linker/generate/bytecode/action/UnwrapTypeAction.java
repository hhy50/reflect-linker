package io.github.hhy.linker.generate.bytecode.action;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import io.github.hhy.linker.runtime.RuntimeUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class UnwrapTypeAction implements Action {
    private final VarInst obj;

    public UnwrapTypeAction(VarInst obj) {
        this.obj = obj;
    }

    @Override
    public void apply(MethodBody body) {
        obj.apply(body);

        Type primitiveType = AsmUtil.getPrimitiveType(obj.getType());
        MethodVisitor mv = body.getWriter();
        switch (primitiveType.getSort()) {
            case Type.BYTE:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.RUNTIME_UTIL_OWNER, "unwrapByte", RuntimeUtil.UNWRAP_BYTE_DESC, false);
                break;
            case Type.SHORT:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.RUNTIME_UTIL_OWNER, "unwrapShort", RuntimeUtil.UNWRAP_SHORT_DESC, false);
                break;
            case Type.INT:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.RUNTIME_UTIL_OWNER, "unwrapInt", RuntimeUtil.UNWRAP_INT_DESC, false);
                break;
            case Type.LONG:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.RUNTIME_UTIL_OWNER, "unwrapLong", RuntimeUtil.UNWRAP_LONG_DESC, false);
                break;
            case Type.FLOAT:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.RUNTIME_UTIL_OWNER, "unwrapFloat", RuntimeUtil.UNWRAP_FLOAT_DESC, false);
                break;
            case Type.DOUBLE:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.RUNTIME_UTIL_OWNER, "unwrapDouble", RuntimeUtil.UNWRAP_DOUBLE_DESC, false);
                break;
            case Type.CHAR:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.RUNTIME_UTIL_OWNER, "unwrapChar", RuntimeUtil.UNWRAP_CHAR_DESC, false);
                break;
            case Type.BOOLEAN:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.RUNTIME_UTIL_OWNER, "unwrapBool", RuntimeUtil.UNWRAP_BOOL_DESC, false);
                break;
            default:
                break;
        }
    }
}