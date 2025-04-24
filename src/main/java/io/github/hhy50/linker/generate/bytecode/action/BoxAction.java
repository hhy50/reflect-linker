package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.runtime.RuntimeUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * The type Wrap type action.
 */
public class BoxAction implements LoadAction {
    private final LoadAction obj;
    private final Type wrapperType;

    /**
     * Instantiates a new Wrap type action.
     *
     * @param obj the obj
     */
    public BoxAction(LoadAction obj, Type wrapperType) {
        this.obj = obj;
        this.wrapperType = wrapperType;
    }

    @Override
    public void load(MethodBody body) {
        obj.load(body);

        Type type = obj.getType();
        if (AsmUtil.isPrimitiveType(type)) {
            MethodVisitor mv = body.getWriter();
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.OWNER, "wrap", "("+obj.getType().getDescriptor()+")Ljava/lang/Object;", false);
        }
    }

    @Override
    public Type getType() {
        if (this.wrapperType != null) {
            return this.wrapperType;
        }
        return ObjectVar.TYPE;
    }
}
