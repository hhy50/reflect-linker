package io.github.hhy.linker.generate.bytecode.action;

import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.runtime.RuntimeUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class UnwrapTypeAction implements Action {
    private final Action obj;
    private final Type type;

    public UnwrapTypeAction(Action obj, Type type) {
        this.obj = obj;
        this.type = type;
    }

    @Override
    public void apply(MethodBody body) {
        obj.apply(body);

        MethodVisitor mv = body.getWriter();
        switch (type.getSort()) {
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
        }
//        mv.visitTypeInsn(Opcodes.CHECKCAST, type.getInternalName());
    }
}