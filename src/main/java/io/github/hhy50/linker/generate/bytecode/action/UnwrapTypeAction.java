package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.RuntimeUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * The type Unwrap type action.
 */
public class UnwrapTypeAction implements Action {
    private final VarInst obj;
    private final Type primitiveType;

    /**
     * Instantiates a new Unwrap type action.
     *
     * @param obj the obj
     */
    public UnwrapTypeAction(VarInst obj) {
        this.obj = obj;
        this.primitiveType = AsmUtil.getPrimitiveType(obj.getType());
    }

    /**
     * Instantiates a new Unwrap type action.
     *
     * @param obj           the obj
     * @param primitiveType the primitive type
     */
    public UnwrapTypeAction(VarInst obj, Type primitiveType) {
        this.obj = obj;
        this.primitiveType = primitiveType;
    }

    @Override
    public void apply(MethodBody body) {
        obj.apply(body);

        MethodVisitor mv = body.getWriter();
        switch (primitiveType.getSort()) {
            case Type.BYTE:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.OWNER, "unwrapByte", RuntimeUtil.UNWRAP_BYTE_DESC, false);
                break;
            case Type.SHORT:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.OWNER, "unwrapShort", RuntimeUtil.UNWRAP_SHORT_DESC, false);
                break;
            case Type.INT:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.OWNER, "unwrapInt", RuntimeUtil.UNWRAP_INT_DESC, false);
                break;
            case Type.LONG:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.OWNER, "unwrapLong", RuntimeUtil.UNWRAP_LONG_DESC, false);
                break;
            case Type.FLOAT:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.OWNER, "unwrapFloat", RuntimeUtil.UNWRAP_FLOAT_DESC, false);
                break;
            case Type.DOUBLE:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.OWNER, "unwrapDouble", RuntimeUtil.UNWRAP_DOUBLE_DESC, false);
                break;
            case Type.CHAR:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.OWNER, "unwrapChar", RuntimeUtil.UNWRAP_CHAR_DESC, false);
                break;
            case Type.BOOLEAN:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.OWNER, "unwrapBool", RuntimeUtil.UNWRAP_BOOL_DESC, false);
                break;
            default:
                break;
        }
    }
}
